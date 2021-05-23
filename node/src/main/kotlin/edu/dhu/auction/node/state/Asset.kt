package edu.dhu.auction.node.state

import edu.dhu.auction.node.contract.AssetContract
import edu.dhu.auction.node.contract.AssetTransferCommand
import net.corda.core.contracts.*
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.AnonymousParty
import java.util.*

@BelongsToContract(AssetContract::class)
data class Asset(
        override val linearId: UniqueIdentifier,
        val name: String,
        override val owner: AnonymousParty,
        override val participants: List<AbstractParty> = listOf(owner)
) : LinearState, OwnableState {

    override fun withNewOwner(newOwner: AbstractParty): CommandAndState {
        return CommandAndState(AssetTransferCommand(), Asset(linearId, name, newOwner as AnonymousParty))
    }

    override fun toString(): String {
        return "Asset{" +
                "linearId=" + linearId +
                ", name='" + name + '\'' +
                ", owner=" + owner +
                '}'
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val asset = other as Asset
        return linearId == asset.linearId &&
                name == asset.name &&
                owner == asset.owner
    }

    override fun hashCode(): Int {
        return Objects.hash(linearId, name, owner)
    }
}