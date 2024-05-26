package org.beobma.stardewvalleyproject.farming

import kotlinx.serialization.Serializable
import org.bukkit.block.Block

@Serializable
data class Plant(
    val name: String,
    val seedPrice: Int,
    var harvestCycle: Int,
    val yield: Int,
    val price: Int,
    val netProfit: Int,
    var isPlant: Boolean = false,
    var block: Block? = null,
    var isHarvestComplete: Boolean = false
) {

    fun Block.plant() {
        if (!this.isSolid) return

        isPlant = true
        block = this
    }

    fun growth() {
        if (!this.isPlant) return
        if (block == null) return
        if (isHarvestComplete) return

        harvestCycle -= 1

        if (harvestCycle <= 0) {
            isHarvestComplete = true
        }
    }
}