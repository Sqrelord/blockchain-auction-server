package edu.dhu.auction.node.flow

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.accounts.workflows.accountService
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount
import edu.dhu.auction.node.contract.AuctionBidCommand
import edu.dhu.auction.node.state.AuctionState
import net.corda.core.contracts.Amount
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.*
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.util.*

@InitiatingFlow
@StartableByRPC
class AuctionBidFlow(private val auctionId: UUID, private val bidderId: UUID, private val bidAmount: Amount<Currency>) : FlowLogic<SignedTransaction>() {
    @Suspendable
    @Throws(FlowException::class)
    override fun call(): SignedTransaction {
        val accountInfo = accountService.accountInfo(bidderId)!!.state.data
        val bidder = subFlow(RequestKeyForAccount(accountInfo))
        val criteria = QueryCriteria.LinearStateQueryCriteria(null, listOf(auctionId))
        val vaultState: StateAndRef<AuctionState> = serviceHub.vaultService.queryBy(AuctionState::class.java, criteria).states
                .stream().findAny().orElseThrow { FlowException("Auction Not Exist:${auctionId}") }
        val input = vaultState.state.data
        val bidders = input.bidders!!.plus(bidder).distinct()

        val output = AuctionState(
                auctionId = input.auctionId,
                assetId = input.assetId,
                basePrice = input.basePrice,
                highestBid = bidAmount,
                highestBidder = bidder,
                bidEndTime = input.bidEndTime,
                winningBid = input.winningBid,
                active = input.active,
                auctioneer = input.auctioneer,
                bidders = bidders,
                winner = input.winner
        )
        val builder = TransactionBuilder(vaultState.state.notary)
                .addInputState(vaultState)
                .addOutputState(output)
                .addCommand(AuctionBidCommand(), bidder.owningKey)

        builder.verify(serviceHub)

        val locallySignedTx = serviceHub.signInitialTransaction(builder, bidder.owningKey)
        val sessions = input.bidders
                .map { initiateFlow(it) }
                .filter { it.counterparty != ourIdentity }

        val fullySignedTx = subFlow(CollectSignaturesFlow(locallySignedTx, sessions, setOf(bidder.owningKey)))
        val finalSignedTx = subFlow(FinalityFlow(fullySignedTx, sessions))
        subFlow(ShareStateFlow(finalSignedTx.tx.outRef(output)))
        return finalSignedTx
    }
}

@InitiatedBy(AuctionBidFlow::class)
class AuctionBidFlowResponder(private val flowSession: FlowSession) : FlowLogic<SignedTransaction>() {
    @Suspendable
    @Throws(FlowException::class)
    override fun call(): SignedTransaction {
        return subFlow(ReceiveFinalityFlow(flowSession))
    }
}