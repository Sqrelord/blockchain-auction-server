package edu.dhu.auction.node.contract

import edu.dhu.auction.node.state.Asset
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction
import org.apache.logging.log4j.LogManager

class AssetContract : Contract {
    @Throws(IllegalArgumentException::class)
    override fun verify(tx: LedgerTransaction) {
        require(tx.commands.isNotEmpty()) { "Commands is Empty" }
        when (tx.commands.requireSingleCommand(CommandData::class.java).value) {
            is AssetCreateCommand -> requireThat {
                log.info("Creating Asset")
                "Asset Must Have Name" using (tx.outputsOfType(Asset::class.java).single().name.isNotBlank())
            }
            is AssetTransferCommand -> requireThat {
                log.info("Transferring Asset")
                val input = tx.inputsOfType(Asset::class.java).single()
                val output = tx.outputsOfType(Asset::class.java).single()
                "Owner and NewOwner Signature Required" using (tx.commands.single().signers.containsAll(listOf(input.owner.owningKey, output.owner.owningKey)))
            }
            else -> {
                log.error("Command Error")
                throw IllegalArgumentException("Command Error")
            }
        }
    }

    companion object {
        private val log = LogManager.getLogger(AssetContract::class.java)
    }
}