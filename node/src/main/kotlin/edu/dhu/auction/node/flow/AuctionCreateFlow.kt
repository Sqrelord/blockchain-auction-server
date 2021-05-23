package edu.dhu.auction.node.flow

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.accounts.workflows.accountService
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount
import edu.dhu.auction.node.contract.AuctionCreateCommand
import edu.dhu.auction.node.state.AuctionState
import net.corda.core.contracts.Amount
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.identity.AnonymousParty
import net.corda.core.internal.randomOrNull
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@InitiatingFlow
@StartableByRPC
class AuctionCreateFlow(private val assetId: UUID, private val auctioneerId: UUID, private val basePrice: Amount<Currency>, private val bidDeadLine: LocalDateTime) : FlowLogic<SignedTransaction>() {
    @Suspendable
    @Throws(FlowException::class)
    override fun call(): SignedTransaction {
        val notary = serviceHub.networkMapCache.notaryIdentities.randomOrNull()
        val accountInfo = accountService.accountInfo(auctioneerId)!!.state.data
        val auctioneer = subFlow(RequestKeyForAccount(accountInfo))
        val bidders = accountService.allAccounts()
                .map { (state) -> state.data }
                .filter { it != accountInfo }
                .map { AnonymousParty(serviceHub.keyManagementService.freshKeyAndCert(ourIdentityAndCert, false, accountInfo.linearId.id).owningKey) }
        val auctionState = AuctionState(
                auctionId = UniqueIdentifier(assetId.toString()),
                assetId = UniqueIdentifier(null, assetId),
                basePrice = basePrice,
                highestBidder = null,
                highestBid = null,
                bidEndTime = bidDeadLine.atZone(ZoneId.systemDefault()).toInstant(),
                winningBid = null,
                active = true,
                auctioneer = auctioneer,
                bidders = bidders,
                winner = null
        )
        val builder = TransactionBuilder(notary)
                .addOutputState(auctionState)
                .addCommand(AuctionCreateCommand(), auctioneer.owningKey)

        builder.verify(serviceHub)

        val locallySignedTx = serviceHub.signInitialTransaction(builder, auctioneer.owningKey)
        val sessions = serviceHub.networkMapCache.allNodes
                .map { it.legalIdentities.first() }
                .filter { it != ourIdentity }
                .map { initiateFlow(it) }

        val fullySignedTx = subFlow(CollectSignaturesFlow(locallySignedTx, sessions, setOf(auctioneer.owningKey)))
        val finalSignedTx = subFlow(FinalityFlow(fullySignedTx, sessions))
        subFlow(ShareStateFlow(finalSignedTx.tx.outRef(auctionState)))
        return finalSignedTx
    }
}

@InitiatedBy(AuctionCreateFlow::class)
class AuctionCreateFlowResponder(private val flowSession: FlowSession) : FlowLogic<SignedTransaction>() {
    @Suspendable
    @Throws(FlowException::class)
    override fun call(): SignedTransaction {
        return subFlow(ReceiveFinalityFlow(flowSession))
    }
}