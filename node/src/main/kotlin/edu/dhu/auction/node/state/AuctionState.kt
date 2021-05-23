package edu.dhu.auction.node.state

import edu.dhu.auction.node.contract.AuctionContract
import edu.dhu.auction.node.flow.AuctionCloseFlow
import net.corda.core.contracts.*
import net.corda.core.flows.FlowLogicRefFactory
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.AnonymousParty
import java.time.Instant
import java.util.*

@BelongsToContract(AuctionContract::class)
data class AuctionState(
        val auctionId: UniqueIdentifier,
        val assetId: UniqueIdentifier,
        val basePrice: Amount<Currency>,
        val highestBid: Amount<Currency>?,
        val highestBidder: AnonymousParty?,
        val bidEndTime: Instant?,
        val winningBid: Amount<Currency>?,
        val active: Boolean,
        val auctioneer: AnonymousParty?,
        val bidders: List<AnonymousParty>?,
        val winner: AnonymousParty?,
        override val participants: List<AbstractParty> = (bidders!! + auctioneer!!)
) : SchedulableState, LinearState {

    override val linearId: UniqueIdentifier get() = auctionId

    override fun nextScheduledActivity(thisStateRef: StateRef, flowLogicRefFactory: FlowLogicRefFactory): ScheduledActivity? {
        if (active) {
            val logicRef = flowLogicRefFactory.create(AuctionCloseFlow::class.java, auctionId.id)
            return ScheduledActivity(logicRef, bidEndTime!!)
        }
        return null
    }


    override fun toString(): String {
        return "AuctionState{" +
                "auctionId=" + auctionId +
                ", assetId=" + assetId +
                ", basePrice=" + basePrice +
                ", highestBid=" + highestBid +
                ", highestBidder=" + highestBidder +
                ", bidEndTime=" + bidEndTime +
                ", winningBid=" + winningBid +
                ", active=" + active +
                ", auctioneer=" + auctioneer +
                ", bidders=" + bidders +
                ", winner=" + winner +
                '}'
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as AuctionState
        return auctionId == that.auctionId &&
                assetId == that.assetId &&
                basePrice == that.basePrice &&
                highestBid == that.highestBid &&
                highestBidder == that.highestBidder &&
                bidEndTime == that.bidEndTime &&
                winningBid == that.winningBid &&
                active == that.active &&
                auctioneer == that.auctioneer &&
                bidders == that.bidders &&
                winner == that.winner
    }

    override fun hashCode(): Int {
        return Objects.hash(auctionId, assetId, basePrice, highestBid, highestBidder,
                bidEndTime, winningBid, active, auctioneer, bidders, winner)
    }
}