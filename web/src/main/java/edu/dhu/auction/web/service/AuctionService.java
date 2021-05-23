package edu.dhu.auction.web.service;

import edu.dhu.auction.web.bean.Account;
import edu.dhu.auction.web.bean.AuctionDetail;
import edu.dhu.auction.web.bean.Lot;

import java.util.List;
import java.util.Set;

public interface AuctionService {
    List<AuctionDetail> bid(Account account, Long lotId, AuctionDetail auctionDetail);

    List<AuctionDetail> getAuctionDetails(Long lotId);

    Lot participate(Account account, Long lotId);

    Set<Account> getParticipates(Long lotId);

    Set<Lot> getJoinAuction(Account account);
}
