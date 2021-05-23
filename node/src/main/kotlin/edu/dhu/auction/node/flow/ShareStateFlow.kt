package edu.dhu.auction.node.flow

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.accounts.workflows.flows.ShareStateAndSyncAccounts
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.Party

@InitiatingFlow
@StartableByRPC
class ShareStateFlow(private val stateAndRef: StateAndRef<ContractState>) : FlowLogic<Unit>() {
    @Suspendable
    @Throws(FlowException::class)
    override fun call() {
        val parties: List<Party> = serviceHub.networkMapCache.allNodes
                .map { it.legalIdentities.first() }
                .filter { it != ourIdentity }
        for (party in parties) {
            subFlow(ShareStateAndSyncAccounts(stateAndRef, party))
        }
    }
}