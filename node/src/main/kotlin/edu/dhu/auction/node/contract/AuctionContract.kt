package edu.dhu.auction.node.contract

import edu.dhu.auction.node.state.AuctionState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction
import org.apache.logging.log4j.LogManager

class AuctionContract : Contract {
    @Throws(IllegalArgumentException::class)
    override fun verify(tx: LedgerTransaction) {
        require(tx.commands.isNotEmpty()) { "Commands is Empty" }
        when (tx.commands.requireSingleCommand(CommandData::class.java).value) {
            is AuctionCreateCommand -> {
                log.info("Creating auction")
            }
            is AuctionBidCommand -> requireThat {
                log.info("Bidding auction")
                val input = tx.inputsOfType(AuctionState::class.java).single()
                val output = tx.outputsOfType(AuctionState::class.java).single()
                "Auction has Ended" using (input.active)
                "Bid Price should be greater than base price" using (output.highestBid!!.quantity > input.basePrice.quantity)
                if (input.highestBid != null) {
                    "Bid Price should be greater than previous highest bid" using (output.highestBid.quantity > input.highestBid.quantity)
                }
            }
            is AuctionCloseCommand -> requireThat {
                log.info("Closing auction")
                val input = tx.inputsOfType(AuctionState::class.java).single()
                val output = tx.outputsOfType(AuctionState::class.java).single()
                "Auction has Ended" using (input.active)
                "Auctioneer Signature Required" using (tx.commands.single().signers.contains(output.auctioneer!!.owningKey))
                "All Bidders Signature Required" using (tx.commands.single().signers.containsAll(input.bidders!!.map { it.owningKey }))
            }
            is AuctionFinalizeCommand -> requireThat {
                log.info("Finalizing auction")
                val input = tx.inputsOfType(AuctionState::class.java).single()
                "Auction is Active" using (!input.active)
                "Auctioneer Signature Required" using (tx.commands.single().signers.contains(input.auctioneer!!.owningKey))
            }
            else -> {
                log.error("Command Error")
                throw IllegalArgumentException("Command Error")
            }
        }
    }

    companion object {
        private val log = LogManager.getLogger(AuctionState::class.java)
    }
}