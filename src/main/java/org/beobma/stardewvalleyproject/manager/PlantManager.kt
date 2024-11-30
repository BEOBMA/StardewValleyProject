package org.beobma.stardewvalleyproject.manager

import org.beobma.stardewvalleyproject.StardewValley
import org.beobma.stardewvalleyproject.manager.DataManager.gameData
import org.beobma.stardewvalleyproject.plant.DeadGrassPlant
import org.beobma.stardewvalleyproject.plant.Plant
import org.bukkit.Material
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

object PlantManager : PlantHandler {
    private val registerPlantList: MutableList<Plant> = mutableListOf()

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
        if (gameData.blockToPlantMap[block] is Plant) return

        // 15% 확률로 잡초 생성
        if (Random.nextInt(100) < 15) {
            isWeeds = true
        }

        isPlant = true
        gameData.plantList.add(this@plant)
        gameData.blockToPlantMap[block] = this@plant
    }

    override fun Plant.growth() {
        if (!this.isPlant) return
        if (block !is Block) return
        if (this == DeadGrassPlant()) return

        val block = block ?: return

        // 작물 죽음
        if (!plantSeasons.contains(gameData.season)) {
            wither()
            return
        }

        if (isHarvestComplete) return
        if (!isWater) return

        val world = block.world
        val location = block.location
        val locationX = location.x.toInt()
        val locationY = location.y.toInt()
        val locationZ = location.z.toInt()
        for (x in (locationX - 1)..(locationX + 1)) {
            for (z in (locationZ - 1)..(locationZ + 1)) {
                val block = world.getBlockAt(x, locationY, z)

                if (block.type != Material.FARMLAND) continue
                val plant = gameData.blockToPlantMap[world.getBlockAt(x, locationY, z)] ?: continue

                // 잡초 없음
                if (!plant.isWeeds) {
                    if (harvestCycle > 0) {
                        harvestCycle -= 1
                    }

                    if (harvestCycle <= 0) {
                        isHarvestComplete = true
                    }
                    weedsCount = 0
                    continue
                }

                // 잡초 있음
                weedsCount++

                // 작물 죽음
                if (weedsCount > 2) {
                    wither()
                }
            }
        }

        isWater = false
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
        // 죽은 풀 제거
        if (this.name == DeadGrassPlant().name) {
            gameData.plantList.remove(this@harvesting)
            gameData.blockToPlantMap.remove(this@harvesting.block)
            return
        }
        if (!isHarvestComplete) return

        gameData.plantList.remove(this@harvesting)
        gameData.blockToPlantMap.remove(this@harvesting.block)

        if (this.yield != 1) {
            val yield = Random.nextInt(1, this.yield + 1)

            repeat(yield) {
                player.inventory.addItem(this.getHarvestItem())
            }
            return
        }

        player.inventory.addItem(this.getHarvestItem())
    }

    private fun Plant.wither() {
        val block = this.block ?: return
        val deadGrassPlant = DeadGrassPlant()

        gameData.blockToPlantMap.remove(block)
        gameData.plantList.remove(this)

        block.type = Material.DIRT
        deadGrassPlant.plant(block)
        deadGrassPlant.isHarvestComplete = true
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