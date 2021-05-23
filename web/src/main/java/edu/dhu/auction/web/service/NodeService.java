package edu.dhu.auction.web.service;

import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import edu.dhu.auction.node.state.Asset;
import edu.dhu.auction.node.state.AuctionState;
import edu.dhu.auction.web.bean.Account;
import edu.dhu.auction.web.bean.Lot;
import net.corda.core.contracts.StateAndRef;

import java.math.BigDecimal;
import java.util.UUID;

public interface NodeService {
    AccountInfo createAccount(Account account);

    Asset createAsset(Lot lot);

    AuctionState createAuction(Lot lot);

    StateAndRef<AuctionState> bid(Lot lot, Account bidder, BigDecimal amount);

    void finalizeAuction(UUID auctionIdentifier);
}
