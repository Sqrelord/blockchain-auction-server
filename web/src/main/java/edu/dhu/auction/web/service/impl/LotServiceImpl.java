package edu.dhu.auction.web.service.impl;

import edu.dhu.auction.node.state.Asset;
import edu.dhu.auction.node.state.AuctionState;
import edu.dhu.auction.web.bean.Account;
import edu.dhu.auction.web.bean.Category;
import edu.dhu.auction.web.bean.Comment;
import edu.dhu.auction.web.bean.Lot;
import edu.dhu.auction.web.repository.AccountRepository;
import edu.dhu.auction.web.repository.CategoryRepository;
import edu.dhu.auction.web.repository.CommentRepository;
import edu.dhu.auction.web.repository.LotRepository;
import edu.dhu.auction.web.service.LotService;
import edu.dhu.auction.web.service.NodeService;
import edu.dhu.auction.web.util.AssertException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LotServiceImpl implements LotService {
    @Resource
    private AccountRepository accountRepository;
    @Resource
    private CategoryRepository categoryRepository;
    @Resource
    private CommentRepository commentRepository;
    @Resource
    private LotRepository lotRepository;
    @Resource
    private NodeService nodeService;

    @Override
    public Lot addLot(Account account, Lot newLot) {
        newLot.setOwner(account);
        Asset asset = nodeService.createAsset(newLot);
        newLot.setAssetIdentifier(asset.getLinearId().getId());
        AuctionState auction = nodeService.createAuction(newLot);
        newLot.setAuctionIdentifier(auction.getAuctionId().getId());
        return lotRepository.save(newLot);
    }

    @Override
    public Lot getLot(Long lotId) {
        return lotRepository.findById(lotId).orElseThrow(() -> AssertException.lotNotExist(lotId));
    }

    @Override
    public List<Comment> addComment(Account account, Long lotId, Comment comment) {
        Lot lot = lotRepository.findById(lotId).orElseThrow(() -> AssertException.lotNotExist(lotId));
        Account commenter = accountRepository.findById(account.getId()).orElseThrow(() -> AssertException.accountNotExist(account.getUsername()));
        lot.getComments().add(comment);
        commenter.getComments().add(comment);
        comment.setLot(lot);
        comment.setAccount(commenter);
        commentRepository.save(comment);
        return getComments(lotId);
    }

    @Override
    public List<Comment> getComments(Long lotId) {
        return commentRepository.findAllByLot_Id(lotId);
    }

    @Override
    public Lot addLotCategory(Long lotId, String categoryName) {
        Lot lot = lotRepository.findById(lotId).orElseThrow(() -> AssertException.lotNotExist(lotId));
        Category category = categoryRepository.findByName(categoryName);
        lot.setCategory(category);
        category.getLots().add(lot);
        return lotRepository.save(lot);
    }
}
