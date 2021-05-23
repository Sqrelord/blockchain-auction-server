package edu.dhu.auction.node.test

import com.r3.corda.lib.accounts.contracts.states.AccountInfo
import edu.dhu.auction.node.flow.*
import edu.dhu.auction.node.state.Asset
import edu.dhu.auction.node.state.AuctionState
import net.corda.core.contracts.Amount
import net.corda.core.contracts.ContractState
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.StartedMockNode
import java.time.LocalDateTime
import java.util.*

object NodeTestUtils {
    @JvmStatic
    fun <T : ContractState> queryVault(node: StartedMockNode, stateType: Class<T>, criteria: QueryCriteria): List<T> {
        return node.services.vaultService.queryBy(stateType, criteria).states.map { it.state.data }
    }

    @JvmStatic
    fun createAccountInfo(network: MockNetwork, node: StartedMockNode, name: String): AccountInfo {
        val future = node.startFlow(AccountCreateFlow(name))
        network.runNetwork()
        return future.get()
    }

    @JvmStatic
    fun createAsset(network: MockNetwork, node: StartedMockNode, name: String, ownerId: UUID): Asset {
        val future = node.startFlow(AssetCreateFlow(name, ownerId))
        network.runNetwork()
        return future.get().tx.outputsOfType(Asset::class.java).first()
    }

    @JvmStatic
    fun createAuction(network: MockNetwork, node: StartedMockNode, assetId: UUID, auctioneerId: UUID, basePrice: Amount<Currency>, bidDeadLine: LocalDateTime): AuctionState {
        val future = node.startFlow(AuctionCreateFlow(assetId, auctioneerId, basePrice, bidDeadLine))
        network.runNetwork()
        return future.get().tx.outputsOfType(AuctionState::class.java).first()
    }

    @JvmStatic
    fun bidAuction(network: MockNetwork, node: StartedMockNode, auctionId: UUID, bidderId: UUID, bidAmount: Amount<Currency>): AuctionState {
        val future = node.startFlow(AuctionBidFlow(auctionId, bidderId, bidAmount))
        network.runNetwork()
        return future.get().tx.outputsOfType(AuctionState::class.java).first()
    }

    @JvmStatic
    fun closeAuction(network: MockNetwork, node: StartedMockNode, auctionId: UUID): AuctionState {
        val future = node.startFlow(AuctionCloseFlow(auctionId))
        network.runNetwork()
        return future.get().tx.outputsOfType(AuctionState::class.java).first()
    }

    @JvmStatic
    fun finalizeAuction(network: MockNetwork, node: StartedMockNode, auctionId: UUID) {
        node.startFlow(AuctionFinalizeFlow(auctionId))
        network.runNetwork()
    }
}