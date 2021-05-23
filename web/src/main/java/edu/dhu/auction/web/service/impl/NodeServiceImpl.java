package edu.dhu.auction.web.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.UtilitiesKt;
import edu.dhu.auction.node.flow.*;
import edu.dhu.auction.node.state.Asset;
import edu.dhu.auction.node.state.AuctionState;
import edu.dhu.auction.web.bean.Account;
import edu.dhu.auction.web.bean.Lot;
import edu.dhu.auction.web.service.NodeService;
import edu.dhu.auction.web.util.AssertException;
import net.corda.core.contracts.Amount;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.node.services.vault.QueryCriteria.LinearStateQueryCriteria;
import net.corda.core.node.services.vault.QueryCriteria.VaultQueryCriteria;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.util.UUID;

@Service
public class NodeServiceImpl implements NodeService {

    @Resource
    private CordaRPCOps cordaRPCOps;

    @Override
    public AccountInfo createAccount(Account account) {
        cordaRPCOps.startFlowDynamic(AccountCreateFlow.class, account.getUsername());
        ThreadUtil.sleep(2000L);
        QueryCriteria queryCriteria = UtilitiesKt.accountNameCriteria(account.getUsername());
        return cordaRPCOps.vaultQueryByCriteria(queryCriteria, AccountInfo.class).getStates()
                .stream().findAny().orElseThrow(() -> AssertException.accountNotExist(account.getUsername())).getState().getData();
    }

    @Override
    public Asset createAsset(Lot lot) {
        cordaRPCOps.startFlowDynamic(AssetCreateFlow.class, lot.getName(), lot.getOwner().getAccountIdentifier());
        ThreadUtil.sleep(2000L);
        VaultQueryCriteria queryCriteria = new VaultQueryCriteria().withExternalIds(ListUtil.of(lot.getOwner().getAccountIdentifier()));
        return cordaRPCOps.vaultQueryByCriteria(queryCriteria, Asset.class).getStates().stream()
                .map(it -> it.getState().getData())
                .filter(it -> it.getName().equals(lot.getName()))
                .findAny().orElseThrow(() -> AssertException.lotNotExist(lot.getId()));
    }

    @Override
    public AuctionState createAuction(Lot lot) {
        Amount<Currency> basePrice = Amount.fromDecimal(lot.getBasePrice(), Currency.getInstance(Locale.CHINA));
        cordaRPCOps.startFlowDynamic(AuctionCreateFlow.class, lot.getAssetIdentifier(), lot.getOwner().getAccountIdentifier(), basePrice, lot.getAuctionEndTime());
        ThreadUtil.sleep(2000L);
        VaultQueryCriteria queryCriteria = new VaultQueryCriteria().withExternalIds(ListUtil.of(lot.getOwner().getAccountIdentifier()));
        return cordaRPCOps.vaultQueryByCriteria(queryCriteria, AuctionState.class).getStates().stream()
                .map(it -> it.getState().getData())
                .filter(it -> it.getAssetId().getId().equals(lot.getAssetIdentifier()))
                .findAny().orElseThrow(() -> AssertException.auctionNotExist(lot.getAssetIdentifier()));
    }

    @Override
    public StateAndRef<AuctionState> bid(Lot lot, Account bidder, BigDecimal amount) {
        Amount<Currency> bidPrice = Amount.fromDecimal(amount, Currency.getInstance(Locale.CHINA));
        cordaRPCOps.startFlowDynamic(AuctionBidFlow.class, lot.getAuctionIdentifier(), bidder.getAccountIdentifier(), bidPrice);
        ThreadUtil.sleep(2000L);
        LinearStateQueryCriteria queryCriteria = new LinearStateQueryCriteria(null, ListUtil.of(lot.getAuctionIdentifier()));
        return cordaRPCOps.vaultQueryByCriteria(queryCriteria, AuctionState.class).getStates().stream()
                .filter(it -> it.getState().getData().getAssetId().getId().equals(lot.getAssetIdentifier()))
                .findAny().orElseThrow(() -> AssertException.auctionNotExist(lot.getAssetIdentifier()));
    }

    @Override
    public void finalizeAuction(UUID auctionIdentifier) {
        cordaRPCOps.startFlowDynamic(AuctionFinalizeFlow.class, auctionIdentifier);
    }
}
