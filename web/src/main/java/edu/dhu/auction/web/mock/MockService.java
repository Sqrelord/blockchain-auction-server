package edu.dhu.auction.web.mock;

import edu.dhu.auction.node.state.Asset;
import edu.dhu.auction.node.state.AuctionState;
import edu.dhu.auction.web.bean.Account;
import edu.dhu.auction.web.bean.Category;
import edu.dhu.auction.web.bean.Lot;
import edu.dhu.auction.web.repository.AccountRepository;
import edu.dhu.auction.web.repository.CategoryRepository;
import edu.dhu.auction.web.repository.LotRepository;
import edu.dhu.auction.web.service.NodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MockService {
    @Resource
    private AccountRepository accountRepository;
    @Resource
    private CategoryRepository categoryRepository;
    @Resource
    private LotRepository lotRepository;
    @Resource
    private NodeService nodeService;

    @Transactional
    public List<Category> addCategory(List<Category> categories) {
        return categoryRepository.saveAll(categories);
    }

    @Transactional
    public void addLot(List<Lot> lots) {
        Account owner = accountRepository.findByUsername("admin");
        for (Lot lot : lots) {
            lot.setOwner(owner);
            Category category = categoryRepository.findByName(lot.getCategory().getName());
            lot.setCategory(category);
            category.getLots().add(lot);
            Asset asset = nodeService.createAsset(lot);
            lot.setAssetIdentifier(asset.getLinearId().getId());
            AuctionState auctionState = nodeService.createAuction(lot);
            lot.setAuctionIdentifier(auctionState.getAuctionId().getId());
        }
        lotRepository.saveAll(lots);
    }
}
