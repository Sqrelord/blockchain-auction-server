package edu.dhu.auction.web.service.impl;

import edu.dhu.auction.node.state.AuctionState;
import edu.dhu.auction.web.bean.Account;
import edu.dhu.auction.web.bean.AuctionDetail;
import edu.dhu.auction.web.bean.Lot;
import edu.dhu.auction.web.repository.AccountRepository;
import edu.dhu.auction.web.repository.AuctionDetailRepository;
import edu.dhu.auction.web.repository.LotRepository;
import edu.dhu.auction.web.service.AuctionService;
import edu.dhu.auction.web.service.NodeService;
import edu.dhu.auction.web.util.AssertException;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.crypto.CryptoUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.security.PublicKey;
import java.util.List;
import java.util.Set;

@Service
public class AuctionServiceImpl implements AuctionService {
    @Resource
    private AccountRepository accountRepository;
    @Resource
    private AuctionDetailRepository auctionDetailRepository;
    @Resource
    private LotRepository lotRepository;
    @Resource
    private NodeService nodeService;

    @Override
    @Transactional
    public List<AuctionDetail> bid(Account account, Long lotId, AuctionDetail auctionDetail) {
        Lot lot = lotRepository.findById(lotId).orElseThrow(() -> AssertException.lotNotExist(lotId));
        Account bidder = accountRepository.findById(account.getId()).orElseThrow(() -> AssertException.accountNotExist(account.getUsername()));
        lot.setHighestPrice(auctionDetail.getAmount());
        lot.getAuctionDetails().add(auctionDetail);
        StateAndRef<AuctionState> auctionStateStateAndRef = nodeService.bid(lot, bidder, auctionDetail.getAmount());
        String txHash = auctionStateStateAndRef.getRef().toString();
        PublicKey owningKey = auctionStateStateAndRef.getState().getData().getHighestBidder().getOwningKey();
        bidder.getBids().add(auctionDetail);
        auctionDetail.setLot(lot);
        auctionDetail.setBidder(bidder);
        auctionDetail.setPublicKey(CryptoUtils.toStringShort(owningKey));
        auctionDetail.setTxHash(txHash);
        auctionDetailRepository.save(auctionDetail);
        lotRepository.save(lot);
        return getAuctionDetails(lotId);
    }

    @Override
    public List<AuctionDetail> getAuctionDetails(Long lotId) {
        return auctionDetailRepository.findAllByLot_IdOrderByBidTimeDesc(lotId);
    }

    @Override
    public Lot participate(Account account, Long lotId) {
        Lot lot = lotRepository.findById(lotId).orElseThrow(() -> AssertException.lotNotExist(lotId));
        Account participant = accountRepository.findById(account.getId()).orElseThrow(() -> AssertException.accountNotExist(account.getUsername()));
        participant.getJoinAuctions().add(lot);
        lot.getParticipants().add(participant);
        return lotRepository.save(lot);
    }

    @Override
    public Set<Account> getParticipates(Long lotId) {
        Lot lot = lotRepository.findById(lotId).orElseThrow(() -> AssertException.lotNotExist(lotId));
        return lot.getParticipants();
    }

    @Override
    public Set<Lot> getJoinAuction(Account account) {
        final Account finalAccount = account;
        account = accountRepository.findById(account.getId()).orElseThrow(() -> AssertException.accountNotExist(finalAccount.getUsername()));
        return account.getJoinAuctions();
    }
}
