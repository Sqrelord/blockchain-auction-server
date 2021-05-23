package edu.dhu.auction.node.test

import com.google.common.collect.ImmutableList
import net.corda.core.identity.CordaX500Name
import net.corda.core.node.NetworkParameters
import net.corda.testing.node.*
import org.junit.After
import org.junit.Before
import java.time.Instant

abstract class AbstractsTest {
    protected lateinit var network: MockNetwork
    protected lateinit var partyA: StartedMockNode
    protected lateinit var partyB: StartedMockNode
    protected lateinit var partyC: StartedMockNode

    @Before
    fun setup() {
        val mockNetworkParameters = MockNetworkParameters(
                listOf(
                        TestCordapp.findCordapp("com.r3.corda.lib.ci.workflows"),
                        TestCordapp.findCordapp("com.r3.corda.lib.accounts.contracts"),
                        TestCordapp.findCordapp("com.r3.corda.lib.accounts.workflows"),
                        TestCordapp.findCordapp("edu.dhu.auction.node.flow"),
                        TestCordapp.findCordapp("edu.dhu.auction.node.contract")))
                .withNetworkParameters(NetworkParameters(4, emptyList(), 10485760, 10485760 * 50, Instant.now(), 1, emptyMap()))
                .withNotarySpecs(ImmutableList.of(MockNetworkNotarySpec(CordaX500Name.parse("O=Notary,L=Shanghai,C=CN"), false)))
        network = MockNetwork(mockNetworkParameters)
        partyA = network.createPartyNode(CordaX500Name.parse("O=PartyA,L=Shanghai,C=CN"))
        partyB = network.createPartyNode(CordaX500Name.parse("O=PartyB,L=Beijing,C=CN"))
        partyC = network.createPartyNode(CordaX500Name.parse("O=PartyC,L=Guangzhou,C=CN"))
        network.runNetwork()
    }

    @After
    fun destroy() {
        network.stopNodes()
    }
}