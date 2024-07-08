package org.beobma.stardewvalleyproject.farming

import kotlinx.serialization.Serializable
import org.beobma.stardewvalleyproject.StardewValley
import org.bukkit.block.Block
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue

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
    var isHarvestComplete: Boolean = false,
    var isWater: Boolean = false
) {

    fun Block.plant() {
        if (!this.isSolid) return

        isPlant = true
        block = this

        // 작물 심기 구현
    }

    fun growth() {
        if (!this.isPlant) return
        if (block == null) return
        if (isHarvestComplete) return
        if (!isWater) return

        harvestCycle -= 1

        // 성장 과정 구현
        val int = getCustomModelData(block!!)
        if (int != null) {
            setCustomModelData(block!!, int + 1)
        }

        if (harvestCycle <= 0) {
            isHarvestComplete = true
            // 작물 성장 완료 구현
        }
    }

    fun water() {
        if (!this.isPlant) return
        if (block == null) return
        if (isHarvestComplete) return
        if (isWater) return

        isWater = true
        // 물 주기 구현
    }

    fun harvesting() {
        if (!this.isPlant) return
        if (block == null) return
        if (!isHarvestComplete) return

        // 수확 구현
    }
}

private fun getCustomModelData(block: Block): Int? {
    val metadataKey = "custom_model_data"
    val metadata: List<MetadataValue> = block.getMetadata(metadataKey)
    for (value in metadata) {
        if (value.owningPlugin == StardewValley.instance) {
            return value.asInt()
        }
    }
    return null
}

private fun setCustomModelData(block: Block, modelData: Int) {
    val metadataKey = "custom_model_data"
    block.setMetadata(metadataKey, FixedMetadataValue(StardewValley.instance, modelData))
}