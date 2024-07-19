package org.beobma.stardewvalleyproject.manager

import org.beobma.stardewvalleyproject.StardewValley
import org.beobma.stardewvalleyproject.plant.Plant
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue
import kotlin.random.Random

interface PlantHandler {
    fun Plant.plant(block: Block)
    fun Plant.growth()
    fun Plant.water()
    fun Plant.harvesting(player: Player)
}

class DefaultPlantHanler : PlantHandler {

    override fun Plant.plant(block: Block) {
        if (!block.isSolid) return
        isPlant = true

        DefaultDataHandler.gameData.plantList.add(this@plant)
        DefaultDataHandler.gameData.blockToPlantMap[block] = this@plant
    }

    override fun Plant.growth() {
        if (!this.isPlant) return
        if (block !is Block) return
        if (isHarvestComplete) return
        if (!isWater) return

        val int = getCustomModelData(block!!)

        if (harvestCycle > 0) {
            harvestCycle -= 1
        }

        if (harvestCycle <= 0) {
            isHarvestComplete = true
        }

        if (int != null) {
            setCustomModelData(block!!, harvestCycle)
        }
    }

    override fun Plant.water() {
        if (!this.isPlant) return
        if (block == null) return
        if (isHarvestComplete) return
        if (isWater) return

        isWater = true
        // 물 주기 구현
    }

    override fun Plant.harvesting(player: Player) {
        if (!this.isPlant) return
        if (block !is Block) return
        if (!isHarvestComplete) return

        DefaultDataHandler.gameData.plantList.remove(this@harvesting)
        DefaultDataHandler.gameData.blockToPlantMap.remove(this@harvesting.block)

        if (this.yield != 1) {
            val yield = Random.nextInt(1, this.yield + 1)

            repeat(yield) {
                player.inventory.addItem(this.harvestItems)
            }
            return
        }

        player.inventory.addItem(this.harvestItems)
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
}

class PlantManager(private val handler: PlantHandler) {
    fun Plant.plant(block: Block) {
        handler.run { this@plant.plant(block) }
    }

    fun Plant.growth() {
        handler.run { this@growth.growth() }
    }

    fun Plant.water() {
        handler.run { this@water.water() }
    }

    fun Plant.harvesting(player: Player) {
        handler.run { this@harvesting.harvesting(player) }
    }
}