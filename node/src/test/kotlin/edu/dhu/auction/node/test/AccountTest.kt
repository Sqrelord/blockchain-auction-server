package edu.dhu.auction.node.test

import com.r3.corda.lib.accounts.workflows.services.AccountService
import com.r3.corda.lib.accounts.workflows.services.KeyManagementBackedAccountService
import edu.dhu.auction.node.test.NodeTestUtils.createAccountInfo
import org.junit.Test
import kotlin.test.assertEquals

class AccountTest : AbstractsTest() {
    @Test
    fun `create account`() {
        val accountInfo = createAccountInfo(network, partyA, "a")
        val accountService: AccountService = partyA.services.cordaService(KeyManagementBackedAccountService::class.java)
        val queryAccountInfo = accountService.accountInfo("a").first().state.data
        assertEquals(accountInfo, queryAccountInfo)
    }
}