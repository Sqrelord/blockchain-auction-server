package edu.dhu.auction.node.flow

import co.paralleluniverse.fibers.Suspendable
import edu.dhu.auction.node.contract.AuctionFinalizeCommand
import edu.dhu.auction.node.state.AuctionState
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.*
import net.corda.core.node.services.vault.QueryCriteria.LinearStateQueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.util.*

@InitiatingFlow
@StartableByRPC
class AuctionFinalizeFlow(private val auctionId: UUID) : FlowLogic<SignedTransaction>() {
    @Suspendable
    @Throws(FlowException::class)
    override fun call(): SignedTransaction {
        val criteria = LinearStateQueryCriteria().withUuid(listOf(auctionId))
        val vaultState: StateAndRef<AuctionState> = serviceHub.vaultService.queryBy(AuctionState::class.java, criteria).states
                .stream().findAny().orElseThrow { IllegalArgumentException("Auction Not Exist:${auctionId}") }
        val input = vaultState.state.data
        val signers = listOf(input.auctioneer!!.owningKey, input.winner!!.owningKey)
        val builder = TransactionBuilder(vaultState.state.notary)
                .addInputState(vaultState)
                .addCommand(AuctionFinalizeCommand(), signers)

        builder.verify(serviceHub)

        val locallySignedTx = serviceHub.signInitialTransaction(builder, signers)
        val sessions = serviceHub.networkMapCache.allNodes
                .map { it.legalIdentities.first() }
                .filter { it != ourIdentity }
                .map { initiateFlow(it) }

        val fullySignedTx = subFlow(CollectSignaturesFlow(locallySignedTx, sessions, signers))
        val finalSignedTx = subFlow(FinalityFlow(fullySignedTx, sessions))
        subFlow(AssetTransferFlow(input.assetId.id, input.winner))
        return finalSignedTx
    }
}

@InitiatedBy(AuctionFinalizeFlow::class)
class AuctionFinalizeFlowResponder(private val flowSession: FlowSession) : FlowLogic<SignedTransaction>() {
    @Suspendable
    @Throws(FlowException::class)
    override fun call(): SignedTransaction {
        return subFlow(ReceiveFinalityFlow(flowSession))
    }
}