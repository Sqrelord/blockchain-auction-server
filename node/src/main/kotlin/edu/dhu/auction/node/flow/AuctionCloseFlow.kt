package edu.dhu.auction.node.flow

import co.paralleluniverse.fibers.Suspendable
import edu.dhu.auction.node.contract.AuctionCloseCommand
import edu.dhu.auction.node.state.AuctionState
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.*
import net.corda.core.node.services.vault.QueryCriteria.LinearStateQueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.util.*

@InitiatingFlow
@SchedulableFlow
class AuctionCloseFlow(private val auctionId: UUID) : FlowLogic<SignedTransaction>() {
    @Suspendable
    @Throws(FlowException::class)
    override fun call(): SignedTransaction {
        val criteria = LinearStateQueryCriteria().withUuid(listOf(auctionId))
        val vaultState: StateAndRef<AuctionState> = serviceHub.vaultService.queryBy(AuctionState::class.java, criteria).states
                .stream().findAny().orElseThrow { FlowException("Auction Not Exist:${auctionId}") }
        val input = vaultState.state.data
        val output = AuctionState(
                auctionId = input.auctionId,
                assetId = input.assetId,
                basePrice = input.basePrice,
                highestBid = input.highestBid,
                highestBidder = input.highestBidder,
                bidEndTime = input.bidEndTime,
                winningBid = input.highestBid,
                active = false,
                auctioneer = input.auctioneer,
                bidders = input.bidders,
                winner = input.highestBidder
        )
        val auctioneerKey = input.auctioneer!!.owningKey
        val signers = input.bidders!!.map { it.owningKey }.plus(auctioneerKey)
        val builder = TransactionBuilder(vaultState.state.notary)
                .addInputState(vaultState)
                .addOutputState(output)
                .addCommand(AuctionCloseCommand(), signers)

        builder.verify(serviceHub)

        val locallySignedTx = serviceHub.signInitialTransaction(builder, signers)
        val sessions = input.bidders
                .map { initiateFlow(it) }
                .filter { it.counterparty != ourIdentity }

        val fullySignedTx = subFlow(CollectSignaturesFlow(locallySignedTx, sessions, signers))
        val finalSignedTx = subFlow(FinalityFlow(fullySignedTx, sessions))
        subFlow(ShareStateFlow(finalSignedTx.tx.outRef(output)))
        return finalSignedTx
    }
}

@InitiatedBy(AuctionCloseFlow::class)
class AuctionCloseFlowResponder(private val flowSession: FlowSession) : FlowLogic<SignedTransaction>() {
    @Suspendable
    @Throws(FlowException::class)
    override fun call(): SignedTransaction {
        return subFlow(ReceiveFinalityFlow(flowSession))
    }
}