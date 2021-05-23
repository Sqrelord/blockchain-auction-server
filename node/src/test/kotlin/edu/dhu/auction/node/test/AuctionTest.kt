package edu.dhu.auction.node.test

import edu.dhu.auction.node.state.Asset
import edu.dhu.auction.node.state.AuctionState
import edu.dhu.auction.node.test.NodeTestUtils.bidAuction
import edu.dhu.auction.node.test.NodeTestUtils.closeAuction
import edu.dhu.auction.node.test.NodeTestUtils.createAccountInfo
import edu.dhu.auction.node.test.NodeTestUtils.createAsset
import edu.dhu.auction.node.test.NodeTestUtils.createAuction
import edu.dhu.auction.node.test.NodeTestUtils.finalizeAuction
import edu.dhu.auction.node.test.NodeTestUtils.queryVault
import net.corda.core.contracts.Amount
import net.corda.core.node.services.Vault.StateStatus
import net.corda.core.node.services.vault.QueryCriteria.LinearStateQueryCriteria
import org.junit.Test
import java.time.LocalDateTime
import java.util.concurrent.ExecutionException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class AuctionTest : AbstractsTest() {
    @Test
    fun `create auction`() {
        val a = createAccountInfo(network, partyA, "a")
        val b = createAccountInfo(network, partyB, "b")
        val c = createAccountInfo(network, partyC, "c")

        val assetA = createAsset(network, partyA, "assetA", a.identifier.id)
        val assetB = createAsset(network, partyB, "assetB", b.identifier.id)
        val assetC = createAsset(network, partyC, "assetC", c.identifier.id)

        val auctionStateA = createAuction(network, partyA, assetA.linearId.id, a.identifier.id, Amount.parseCurrency("100 CNY"), LocalDateTime.now().plusMinutes(5L))
        val auctionStateB = createAuction(network, partyB, assetB.linearId.id, b.identifier.id, Amount.parseCurrency("100 CNY"), LocalDateTime.now().plusMinutes(5L))
        val auctionStateC = createAuction(network, partyC, assetC.linearId.id, c.identifier.id, Amount.parseCurrency("100 CNY"), LocalDateTime.now().plusMinutes(5L))

        assertEquals(assetA.linearId, auctionStateA.assetId)
        assertEquals(assetB.linearId, auctionStateB.assetId)
        assertEquals(assetC.linearId, auctionStateC.assetId)
    }

    @Test
    fun `bid auction`() {
        val a = createAccountInfo(network, partyA, "a")
        val b = createAccountInfo(network, partyB, "b")
        val c = createAccountInfo(network, partyC, "c")
        val d = createAccountInfo(network, partyB, "d")

        val assetA = createAsset(network, partyA, "assetA", a.identifier.id)
        val assetB = createAsset(network, partyB, "assetB", b.identifier.id)

        val auctionStateA = createAuction(network, partyA, assetA.linearId.id, a.identifier.id, Amount.parseCurrency("200 USD"), LocalDateTime.now().plusMinutes(10))
        val auctionStateB = createAuction(network, partyB, assetB.linearId.id, b.identifier.id, Amount.parseCurrency("200 USD"), LocalDateTime.now().plusMinutes(10))

        val bidResultB = bidAuction(network, partyB, auctionStateA.auctionId.id, b.identifier.id, Amount.parseCurrency("300 USD"))
        val bidResultC = bidAuction(network, partyC, auctionStateA.auctionId.id, c.identifier.id, Amount.parseCurrency("400 USD"))
        val bidResultD = bidAuction(network, partyB, auctionStateB.auctionId.id, d.identifier.id, Amount.parseCurrency("500 USD"))

        assertEquals(bidResultB.highestBid, Amount.parseCurrency("300 USD"))
        assertEquals(bidResultC.highestBid, Amount.parseCurrency("400 USD"))
        assertEquals(bidResultD.highestBid, Amount.parseCurrency("500 USD"))
    }

    @Test
    fun `close auction and bid after auction closed`() {
        val a = createAccountInfo(network, partyA, "a")
        val b = createAccountInfo(network, partyB, "b")
        val c = createAccountInfo(network, partyC, "c")

        val assetA = createAsset(network, partyA, "assetA", a.identifier.id)
        val assetB = createAsset(network, partyB, "assetB", b.identifier.id)
        val assetC = createAsset(network, partyC, "assetC", c.identifier.id)

        val auctionStateA = createAuction(network, partyA, assetA.linearId.id, a.identifier.id, Amount.parseCurrency("100 CNY"), LocalDateTime.now().plusMinutes(5L))
        val auctionStateB = createAuction(network, partyB, assetB.linearId.id, b.identifier.id, Amount.parseCurrency("100 CNY"), LocalDateTime.now().plusMinutes(5L))
        val auctionStateC = createAuction(network, partyC, assetC.linearId.id, c.identifier.id, Amount.parseCurrency("100 CNY"), LocalDateTime.now().plusMinutes(5L))

        val closeAuctionA = closeAuction(network, partyA, auctionStateA.auctionId.id)
        val closeAuctionB = closeAuction(network, partyB, auctionStateB.auctionId.id)
        val closeAuctionC = closeAuction(network, partyC, auctionStateC.auctionId.id)

        assertFalse(closeAuctionA.active)
        assertFalse(closeAuctionB.active)
        assertFalse(closeAuctionC.active)

        assertFailsWith<ExecutionException>("Auction has Ended") { bidAuction(network, partyC, auctionStateA.auctionId.id, c.identifier.id, Amount.parseCurrency("200 CNY")) }
        assertFailsWith<ExecutionException>("Auction has Ended") { bidAuction(network, partyA, auctionStateB.auctionId.id, a.identifier.id, Amount.parseCurrency("200 CNY")) }
        assertFailsWith<ExecutionException>("Auction has Ended") { bidAuction(network, partyB, auctionStateC.auctionId.id, b.identifier.id, Amount.parseCurrency("200 CNY")) }
    }

    @Test
    fun `finalize auction`() {
        val a = createAccountInfo(network, partyA, "a")
        val b = createAccountInfo(network, partyA, "b")
        val c = createAccountInfo(network, partyA, "c")

        val assetA = createAsset(network, partyA, "assetA", a.identifier.id)
        val assetB = createAsset(network, partyA, "assetB", b.identifier.id)
        val assetC = createAsset(network, partyA, "assetC", c.identifier.id)

        val auctionStateA = createAuction(network, partyA, assetA.linearId.id, a.identifier.id, Amount.parseCurrency("100 CNY"), LocalDateTime.now().plusMinutes(5L))
        val auctionStateB = createAuction(network, partyA, assetB.linearId.id, b.identifier.id, Amount.parseCurrency("100 CNY"), LocalDateTime.now().plusMinutes(5L))
        val auctionStateC = createAuction(network, partyA, assetC.linearId.id, c.identifier.id, Amount.parseCurrency("100 CNY"), LocalDateTime.now().plusMinutes(5L))

        bidAuction(network, partyA, auctionStateA.auctionId.id, c.identifier.id, Amount.parseCurrency("200 CNY"))
        bidAuction(network, partyA, auctionStateB.auctionId.id, a.identifier.id, Amount.parseCurrency("200 CNY"))
        bidAuction(network, partyA, auctionStateC.auctionId.id, b.identifier.id, Amount.parseCurrency("200 CNY"))

        closeAuction(network, partyA, auctionStateA.auctionId.id)
        closeAuction(network, partyA, auctionStateB.auctionId.id)
        closeAuction(network, partyA, auctionStateC.auctionId.id)

        val criteria = LinearStateQueryCriteria().withUuid(listOf(auctionStateA.auctionId.id, auctionStateB.auctionId.id, auctionStateC.auctionId.id))
        val activeAuctions = queryVault(partyA, AuctionState::class.java, criteria.withStatus(StateStatus.UNCONSUMED))
        assertEquals(3, activeAuctions.size)

        finalizeAuction(network, partyA, auctionStateA.auctionId.id)
        finalizeAuction(network, partyA, auctionStateB.auctionId.id)
        finalizeAuction(network, partyA, auctionStateC.auctionId.id)

        val unconsumedAuctions = queryVault(partyA, AuctionState::class.java, criteria.withStatus(StateStatus.UNCONSUMED))
        assertEquals(0, unconsumedAuctions.size)

        val allAuctions = queryVault(partyA, AuctionState::class.java, criteria.withStatus(StateStatus.ALL))
        assertEquals(9, allAuctions.size)

        val unconsumedAsset = queryVault(partyA, Asset::class.java, LinearStateQueryCriteria().withStatus(StateStatus.UNCONSUMED).withUuid(listOf(assetA.linearId.id, assetB.linearId.id, assetC.linearId.id)))
        val allAsset = queryVault(partyA, Asset::class.java, LinearStateQueryCriteria().withStatus(StateStatus.ALL).withUuid(listOf(assetA.linearId.id, assetB.linearId.id, assetC.linearId.id)))
        assertEquals(3, unconsumedAsset.size)
        assertEquals(6, allAsset.size)
    }
}