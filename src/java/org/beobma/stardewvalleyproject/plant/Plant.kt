package org.beobma.stardewvalleyproject.plant

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack

@Serializable
data class Plant(
    val name: String,
    val seedPrice: Int,
    var harvestCycle: Int,
    val yield: Int,
    val price: Int,
    val netProfit: Int,
    @Contextual val seedItem: ItemStack,
    @Contextual val harvestItems: ItemStack,
    var isPlant: Boolean = false,
    var block: Block? = null,
    var isHarvestComplete: Boolean = false,
    var isWater: Boolean = false
)