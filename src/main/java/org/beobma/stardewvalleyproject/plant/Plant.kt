package org.beobma.stardewvalleyproject.plant


import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack
import java.io.Serializable

abstract class Plant(
    val name: String,
    var harvestCycle: Int,
    val yield: Int,
    val plantSeasons: List<Season>,

    // 추가 변수가 필요할 수 있음
    // ex: 모델 데이터, 심을 수 있는 블록, 심어지는 방향 등...
    var isPlant: Boolean = false,
    var block: Block? = null,
    var isHarvestComplete: Boolean = false,
    var isWater: Boolean = false
) : Serializable {
    abstract fun getSeedItem(): ItemStack
    abstract fun getHarvestItem(): ItemStack
}
