package edu.dhu.auction.node.flow

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.accounts.workflows.accountService
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount
import edu.dhu.auction.node.contract.AssetCreateCommand
import edu.dhu.auction.node.state.Asset
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.internal.randomOrNull
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.util.*

@InitiatingFlow
@StartableByRPC
class AssetCreateFlow(private val name: String, private val ownerId: UUID) : FlowLogic<SignedTransaction>() {
    @Suspendable
    @Throws(FlowException::class)
    override fun call(): SignedTransaction {
        val notary = serviceHub.networkMapCache.notaryIdentities.randomOrNull()
        val account = accountService.accountInfo(ownerId)!!.state.data
        val owner = subFlow(RequestKeyForAccount(account))
        val output = Asset(
                linearId = UniqueIdentifier(),
                name = name,
                owner = owner
        )
        val builder = TransactionBuilder(notary)
                .addOutputState(output)
                .addCommand(AssetCreateCommand(), owner.owningKey)

        builder.verify(serviceHub)

        val sessions = serviceHub.networkMapCache.allNodes
                .map { it.legalIdentities.first() }
                .filter { it != ourIdentity }
                .map { initiateFlow(it) }

        val signedTransaction = serviceHub.signInitialTransaction(builder, owner.owningKey)
        val finalSignedTx = subFlow(FinalityFlow(signedTransaction, sessions))
        subFlow(ShareStateFlow(finalSignedTx.tx.outRef(output)))
        return finalSignedTx
    }
}

@InitiatedBy(AssetCreateFlow::class)
class AssetCreateFlowResponder(private val flowSession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    @Throws(FlowException::class)
    override fun call() {
        subFlow(ReceiveFinalityFlow(flowSession))
    }
}