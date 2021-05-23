package edu.dhu.auction.node.test

import edu.dhu.auction.node.state.Asset
import edu.dhu.auction.node.state.AuctionState
import edu.dhu.auction.node.test.NodeTestUtils.bidAuction
import edu.dhu.auction.node.test.NodeTestUtils.createAccountInfo
import edu.dhu.auction.node.test.NodeTestUtils.createAsset
import edu.dhu.auction.node.test.NodeTestUtils.createAuction
import edu.dhu.auction.node.test.NodeTestUtils.queryVault
import net.corda.core.contracts.Amount
import net.corda.core.node.services.vault.QueryCriteria.LinearStateQueryCriteria
import net.corda.core.node.services.vault.QueryCriteria.VaultQueryCriteria
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class VaultQueryTest : AbstractsTest() {
    @Test
    fun `query asset by owner or linearId`() {
        val a = createAccountInfo(network, partyA, "a")
        val b = createAccountInfo(network, partyB, "b")
        val c = createAccountInfo(network, partyC, "c")

        val assetA = createAsset(network, partyA, "assetA", a.identifier.id)
        val assetB = createAsset(network, partyB, "assetB", b.identifier.id)
        val assetC = createAsset(network, partyC, "assetC", c.identifier.id)

        // query by owner
        val queryAssetAByOwner = queryVault(partyB, Asset::class.java, VaultQueryCriteria().withExternalIds(listOf(a.identifier.id))).first()
        val queryAssetBByOwner = queryVault(partyC, Asset::class.java, VaultQueryCriteria().withExternalIds(listOf(b.identifier.id))).first()
        val queryAssetCByOwner = queryVault(partyA, Asset::class.java, VaultQueryCriteria().withExternalIds(listOf(c.identifier.id))).first()
        assertEquals(assetA, queryAssetAByOwner)
        assertEquals(assetB, queryAssetBByOwner)
        assertEquals(assetC, queryAssetCByOwner)

        // query by linearId
        val queryAssetAById = queryVault(partyB, Asset::class.java, LinearStateQueryCriteria(null, listOf(assetA.linearId.id))).first()
        val queryAssetBById = queryVault(partyC, Asset::class.java, LinearStateQueryCriteria(null, listOf(assetB.linearId.id))).first()
        val queryAssetCById = queryVault(partyA, Asset::class.java, LinearStateQueryCriteria(null, listOf(assetC.linearId.id))).first()
        assertEquals(assetA, queryAssetAById)
        assertEquals(assetB, queryAssetBById)
        assertEquals(assetC, queryAssetCById)
    }

    @Test
    fun `query auction by auctionId or auctioneer`() {
        val a = createAccountInfo(network, partyA, "a")
        val b = createAccountInfo(network, partyB, "b")
        val c = createAccountInfo(network, partyC, "c")

        val assetA = createAsset(network, partyA, "assetA", a.identifier.id)
        val assetB = createAsset(network, partyB, "assetB", b.identifier.id)
        val assetC = createAsset(network, partyC, "assetC", c.identifier.id)

        val auctionA = createAuction(network, partyA, assetA.linearId.id, a.identifier.id, Amount.parseCurrency("100 CNY"), LocalDateTime.now().plusMinutes(5L))
        val auctionB = createAuction(network, partyB, assetB.linearId.id, b.identifier.id, Amount.parseCurrency("100 CNY"), LocalDateTime.now().plusMinutes(5L))
        val auctionC = createAuction(network, partyC, assetC.linearId.id, c.identifier.id, Amount.parseCurrency("100 CNY"), LocalDateTime.now().plusMinutes(5L))

        // query by auctionId
        val queryAuctionAById = queryVault(partyC, AuctionState::class.java, LinearStateQueryCriteria(null, listOf(auctionA.auctionId.id))).first()
        val queryAuctionBById = queryVault(partyA, AuctionState::class.java, LinearStateQueryCriteria(null, listOf(auctionB.auctionId.id))).first()
        val queryAuctionCById = queryVault(partyB, AuctionState::class.java, LinearStateQueryCriteria(null, listOf(auctionC.auctionId.id))).first()
        assertEquals(auctionA, queryAuctionAById)
        assertEquals(auctionB, queryAuctionBById)
        assertEquals(auctionC, queryAuctionCById)

        // query by auctioneer
        val queryAuctionAByAuctioneer = queryVault(partyC, AuctionState::class.java, VaultQueryCriteria().withExternalIds(listOf(a.identifier.id))).first()
        val queryAuctionBByAuctioneer = queryVault(partyC, AuctionState::class.java, VaultQueryCriteria().withExternalIds(listOf(b.identifier.id))).first()
        val queryAuctionCByAuctioneer = queryVault(partyC, AuctionState::class.java, VaultQueryCriteria().withExternalIds(listOf(c.identifier.id))).first()
        assertEquals(auctionA, queryAuctionAByAuctioneer)
        assertEquals(auctionB, queryAuctionBByAuctioneer)
        assertEquals(auctionC, queryAuctionCByAuctioneer)
    }

    @Test
    fun `query bid by bidder`() {
        val a = createAccountInfo(network, partyA, "a")
        val b = createAccountInfo(network, partyB, "b")
        val c = createAccountInfo(network, partyC, "c")

        val assetA = createAsset(network, partyA, "assetA", a.identifier.id)
        val assetB = createAsset(network, partyB, "assetB", b.identifier.id)
        val assetC = createAsset(network, partyC, "assetC", c.identifier.id)

        val auctionA = createAuction(network, partyA, assetA.linearId.id, a.identifier.id, Amount.parseCurrency("100 CNY"), LocalDateTime.now().plusMinutes(5L))
        val auctionB = createAuction(network, partyB, assetB.linearId.id, b.identifier.id, Amount.parseCurrency("100 CNY"), LocalDateTime.now().plusMinutes(5L))
        val auctionC = createAuction(network, partyC, assetC.linearId.id, c.identifier.id, Amount.parseCurrency("100 CNY"), LocalDateTime.now().plusMinutes(5L))

        val bidA = bidAuction(network, partyC, auctionA.auctionId.id, c.identifier.id, Amount.parseCurrency("200 CNY"))
        val bidB = bidAuction(network, partyA, auctionB.auctionId.id, a.identifier.id, Amount.parseCurrency("200 CNY"))
        val bidC = bidAuction(network, partyB, auctionC.auctionId.id, b.identifier.id, Amount.parseCurrency("200 CNY"))

        // query by bidder
        val queryBidAByBidder = queryVault(partyC, AuctionState::class.java, VaultQueryCriteria().withExternalIds(listOf(c.identifier.id)))
        val queryBidBByBidder = queryVault(partyA, AuctionState::class.java, VaultQueryCriteria().withExternalIds(listOf(a.identifier.id)))
        val queryBidCByBidder = queryVault(partyB, AuctionState::class.java, VaultQueryCriteria().withExternalIds(listOf(b.identifier.id)))

        // the query results include auctions participated as bidder and auctioneer
        assertEquals(listOf(bidC, bidA), queryBidAByBidder)
        assertEquals(listOf(bidA, bidB), queryBidBByBidder)
        assertEquals(listOf(bidB, bidC), queryBidCByBidder)
    }
}