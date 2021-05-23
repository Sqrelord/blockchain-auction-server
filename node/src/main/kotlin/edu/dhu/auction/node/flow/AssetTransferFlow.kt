package edu.dhu.auction.node.flow

import co.paralleluniverse.fibers.Suspendable
import edu.dhu.auction.node.state.Asset
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.*
import net.corda.core.identity.AnonymousParty
import net.corda.core.node.services.vault.QueryCriteria.LinearStateQueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.util.*

@InitiatingFlow
class AssetTransferFlow(private val assetId: UUID, private val newOwner: AnonymousParty) : FlowLogic<SignedTransaction>() {
    @Suspendable
    @Throws(FlowException::class)
    override fun call(): SignedTransaction {
        val criteria = LinearStateQueryCriteria().withUuid(listOf(assetId))
        val input: StateAndRef<Asset> = serviceHub.vaultService.queryBy(Asset::class.java, criteria).states
                .stream().findAny().orElseThrow { FlowException("Asset Not Exist:${assetId}") }
        val asset = input.state.data
        val (command, state) = asset.withNewOwner(newOwner)
        val signers = listOf(asset.owner.owningKey, state.owner.owningKey)
        val builder = TransactionBuilder(input.state.notary)
                .addInputState(input)
                .addOutputState(state)
                .addCommand(command, signers)

        builder.verify(serviceHub)

        val locallySignedTx = serviceHub.signInitialTransaction(builder, signers)
        val sessions = listOf(initiateFlow(newOwner)).filter { it.counterparty != ourIdentity }

        val fullySignedTx = subFlow(CollectSignaturesFlow(locallySignedTx, sessions, signers))
        val finalSignedTx = subFlow(FinalityFlow(fullySignedTx, sessions))
        subFlow(ShareStateFlow(finalSignedTx.tx.outRef(state)))
        return finalSignedTx
    }
}

@InitiatedBy(AssetTransferFlow::class)
class AssetTransferFlowResponder(private val flowSession: FlowSession) : FlowLogic<SignedTransaction>() {
    @Suspendable
    @Throws(FlowException::class)
    override fun call(): SignedTransaction {
        return subFlow(ReceiveFinalityFlow(flowSession))
    }
}