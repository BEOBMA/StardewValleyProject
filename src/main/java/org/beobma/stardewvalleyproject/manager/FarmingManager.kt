package org.beobma.stardewvalleyproject.manager

import org.beobma.stardewvalleyproject.manager.DataManager.gameData
import org.beobma.stardewvalleyproject.plant.list.DeadGrassPlant
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.tool.Capsule
import org.beobma.stardewvalleyproject.tool.Hoe
import org.beobma.stardewvalleyproject.tool.WateringCan
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.type.Farmland
import org.bukkit.entity.Player
import kotlin.random.Random

interface FarmingHandler {
    fun Player.tillage(block: Block)
    fun Player.watering(block: Block)
    fun Player.plant(block: Block, plant: Plant)
    fun Player.harvesting(plant: Plant)

//    fun Player.capsule(block: Block)

    fun Plant.growth()
}

object FarmingManager : FarmingHandler {
    private val hoeClass = Hoe()
    private val wateringCanClass = WateringCan()
    private val capsuleClass = Capsule()

    override fun Player.tillage(block: Block) {
        val material = block.type
        val location = block.location
        val handItem = this.inventory.itemInMainHand
        val hoes = hoeClass.hoes

        if (handItem !in hoes) return
        if (material != Material.DIRT) return
        when (handItem) {
            hoeClass.hoe -> {
                // 그냥 괭이
                if (location.block.type == Material.DIRT) {
                    location.block.type = Material.FARMLAND
                }
            }
            hoeClass.durableHoe -> {
                // 단단 괭이
                val direction = this.location.direction.normalize()
                for (i in 0..2) {
                    val targetLocation = location.clone().add(direction.multiply(i.toDouble()))
                    val targetBlock = targetLocation.block
                    if (targetBlock.type == Material.DIRT) {
                        targetBlock.type = Material.FARMLAND
                    }
                }
            }
            hoeClass.lightAndSturdyHoe -> {
                // 가 / 단단한 괭이
                val direction = this.location.direction.normalize()
                val perpendicular = direction.clone().rotateAroundY(Math.PI / 2)
                for (i in 0..2) {
                    for (j in -1..1) {
                        val targetLocation = location.clone().add(direction.multiply(i.toDouble())).add(perpendicular.multiply(j.toDouble()))
                        val targetBlock = targetLocation.block
                        if (targetBlock.type == Material.DIRT) {
                            targetBlock.type = Material.FARMLAND
                        }
                    }
                }
            }
            hoeClass.autoHoe -> {
                // 자동화 괭이
                val direction = this.location.direction.normalize()
                val perpendicular = direction.clone().rotateAroundY(Math.PI / 2)
                for (i in 0..5) {
                    for (j in -1..1) {
                        val targetLocation = location.clone().add(direction.multiply(i.toDouble())).add(perpendicular.multiply(j.toDouble()))
                        val targetBlock = targetLocation.block
                        if (targetBlock.type == Material.DIRT) {
                            targetBlock.type = Material.FARMLAND
                        }
                    }
                }
            }
        }
    }

    override fun Player.watering(block: Block) {
        val handItem = this.inventory.itemInMainHand
        val wateringCans = wateringCanClass.wateringCans

        if (handItem !in wateringCans) return

        val farmland = if (block.type == Material.FARMLAND) {
            block.blockData as? Farmland
        } else {
            val belowBlock = block.world.getBlockAt(block.x, block.y - 1, block.z)
            if (belowBlock.type == Material.FARMLAND) {
                belowBlock.blockData as? Farmland
            } else null
        } ?: return

        if (farmland.moisture == farmland.maximumMoisture) return
        farmland.moisture = farmland.maximumMoisture
        gameData.interactionFarmlands.add(block)
    }

    override fun Player.plant(block: Block, plant: Plant) {
        val cropBlock = block.world.getBlockAt(block.x, block.y + 1, block.z)

        if (block as? Farmland == null) return
        if (cropBlock.type != Material.AIR) return
        if (gameData.blockToPlantMap[block] is Plant) return

        // 15% 확률로 잡초 생성
        if (Random.nextInt(100) < 15) {
            plant.isWeeds = true
        }

        cropBlock.type = Material.WHEAT_SEEDS
        plant.isPlant = true
        gameData.plantList.add(plant)
        gameData.blockToPlantMap[block] = plant
    }

    override fun Player.harvesting(plant: Plant) {
        if (!plant.isPlant) return
        if (plant.block !is Block) return

        val block = plant.block ?: return
        val cropBlock = block.world.getBlockAt(block.x, block.y + 1, block.z)

        if (this.name == DeadGrassPlant().name) {
            gameData.plantList.remove(plant)
            gameData.blockToPlantMap.remove(block)
            cropBlock.type = Material.AIR
            return
        }
        if (!plant.isHarvestComplete) return


        gameData.plantList.remove(plant)
        gameData.blockToPlantMap.remove(block)

        if (plant.yield != 1) {
            val yield = Random.nextInt(1, plant.yield + 1)

            repeat(yield) {
                inventory.addItem(plant.getHarvestItem())
            }
            cropBlock.type = Material.AIR
            return
        }

        inventory.addItem(plant.getHarvestItem())
    }

//    override fun Player.capsule(block: Block) {
//        val handItem = this.inventory.itemInMainHand
//        val capsules = capsuleClass.capsules
//
//        if (handItem !in capsules) return
//
//        val farmland = if (block.type == Material.FARMLAND) {
//            block.blockData as? Farmland
//        } else {
//            val belowBlock = block.world.getBlockAt(block.x, block.y - 1, block.z)
//            if (belowBlock.type == Material.FARMLAND) {
//                belowBlock.blockData as? Farmland
//            } else null
//        } ?: return
//
//        if (farmland.moisture == farmland.maximumMoisture) return
//        farmland.moisture = farmland.maximumMoisture
//        gameData.interactionFarmlands.add(block)
//    }

    override fun Plant.growth() {
        if (!this.isPlant) return
        if (block !is Block) return
        if (block !is Farmland) return
        val block = block ?: return
        val farmland = block as Farmland

        if (!plantSeasons.contains(gameData.season)) {
            if (this.name == DeadGrassPlant().name) return
            wither()
            return
        }

        if (farmland.moisture != farmland.maximumMoisture) return
        if (isHarvestComplete) return

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
        farmland.moisture = 0
    }

    private fun Plant.wither() {
        val block = block ?: return
        val deadGrassPlant = DeadGrassPlant()
        deadGrassPlant.isHarvestComplete = true
        deadGrassPlant.isPlant = true


        val cropBlock = block.world.getBlockAt(block.x, block.y + 1, block.z)

        if (block as? Farmland == null) return
        if (gameData.blockToPlantMap[block] is Plant) return


        cropBlock.type = Material.WHEAT_SEEDS
        gameData.plantList.remove(this)
        gameData.plantList.add(deadGrassPlant)
        block.type = Material.DIRT
        gameData.blockToPlantMap.replace(block, deadGrassPlant)
    }
}