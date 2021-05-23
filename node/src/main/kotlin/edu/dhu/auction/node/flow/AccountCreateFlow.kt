package edu.dhu.auction.node.flow

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.accounts.contracts.states.AccountInfo
import com.r3.corda.lib.accounts.workflows.accountService
import com.r3.corda.lib.accounts.workflows.flows.ShareAccountInfo
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import java.util.concurrent.ExecutionException

@InitiatingFlow
@StartableByRPC
class AccountCreateFlow(private val username: String) : FlowLogic<AccountInfo>() {
    @Suspendable
    @Throws(FlowException::class)
    override fun call(): AccountInfo {
        return try {
            val accountInfo = accountService.createAccount(username).get()
            val parties = serviceHub.networkMapCache.allNodes
                    .map { it.legalIdentities.first() }
                    .filter { it != ourIdentity }
            subFlow(ShareAccountInfo(accountInfo, parties))
            accountInfo.state.data
        } catch (e: InterruptedException) {
            throw FlowException(e.message, e.cause)
        } catch (e: ExecutionException) {
            throw FlowException(e.message, e.cause)
        }
    }
}