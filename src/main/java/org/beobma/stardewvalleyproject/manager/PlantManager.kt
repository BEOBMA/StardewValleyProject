package org.beobma.stardewvalleyproject.manager

import org.beobma.stardewvalleyproject.StardewValley
import org.beobma.stardewvalleyproject.data.GameData
import org.beobma.stardewvalleyproject.manager.DefaultDataHandler.Companion.gameData
import org.beobma.stardewvalleyproject.plant.Plant
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue
import kotlin.random.Random

interface PlantHandler {
    fun Plant.register()
    fun getRegisterPlantList(): List<Plant>
    fun Plant.plant(block: Block)
    fun Plant.growth()
    fun Plant.water()
    fun Plant.harvesting(player: Player)
}

class DefaultPlantHanler : PlantHandler {
    companion object {
        private val registerPlantList: MutableList<Plant> = mutableListOf()
    }

    override fun Plant.register() {
        registerPlantList.add(this)
        registerPlantList.distinct()
        logMessage("[StardewValley] ${this.name} register")
    }

    override fun getRegisterPlantList(): List<Plant> {
        return registerPlantList
    }

    override fun Plant.plant(block: Block) {
        if (!block.isSolid) return
        if (DefaultDataHandler.gameData.blockToPlantMap[block] is Plant) return

        isPlant = true
        DefaultDataHandler.gameData.plantList.add(this@plant)
        DefaultDataHandler.gameData.blockToPlantMap[block] = this@plant
    }

    override fun Plant.growth() {
        if (!this.isPlant) return
        if (block !is Block) return
        if (isHarvestComplete) return
        if (!isWater) return

        if (plantSeason != gameData.season) {
            // 작물 계절 불일치로 시들어야 함.
        }

        // 작물 계절 일치 확인 필요

        val int = getCustomModelData(block!!)

        isWater = false
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
                player.inventory.addItem(this.getHarvestItem())
            }
            return
        }

        player.inventory.addItem(this.getHarvestItem())
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

    private fun logMessage(message: String) {
        StardewValley.instance.loggerMessage(message)
    }
}

object PlantManager {
    private val handler: PlantHandler = DefaultPlantHanler()

    fun Plant.register() {
        handler.run { this@register.register() }
    }

    fun getRegisterPlantList(): List<Plant> {
        return handler.run { getRegisterPlantList() }
    }

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