package org.beobma.stardewvalleyproject.manager

import org.beobma.stardewvalleyproject.manager.DataManager.gameData
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.plant.list.*
import org.beobma.stardewvalleyproject.tool.Capsule
import org.beobma.stardewvalleyproject.tool.CapsuleType
import org.beobma.stardewvalleyproject.tool.Hoe
import org.beobma.stardewvalleyproject.tool.WateringCan
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Ageable
import org.bukkit.block.data.type.Farmland
import org.bukkit.entity.Player
import kotlin.random.Random

interface FarmingHandler {
    fun Player.tillage(block: Block)
    fun Player.watering(block: Block)
    fun Player.plant(block: Block, plant: Plant)
    fun Player.harvesting(plant: Plant)

    fun Player.capsule(plant: Plant)

    fun Plant.isWatering(): Boolean

    fun Plant.growth()
}

object FarmingManager : FarmingHandler {
    private val hoeClass = Hoe()
    private val wateringCanClass = WateringCan()
    private val capsuleClass = Capsule()
    val plants: HashSet<Plant> = hashSetOf(
        BitPlant(), CabbagePlant(), CoffeeBeansPlant(), CornerPlant(), CranberryPlant(), CucumberPlant(), DeadGrassPlant(),
        PotatoPlant(), PumpkinPlant(), TomatoPlant(), WheatPlant()
    )

    override fun Player.tillage(block: Block) {
        if (block.type != Material.DIRT) return

        val handItem = inventory.itemInMainHand
        if (handItem !in hoeClass.hoes) return

        fun convertToFarmland(targetBlock: Block) {
            targetBlock.type = Material.FARMLAND
            gameData.interactionFarmlands.add(targetBlock)
        }

        val offsets = listOf(
            Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
            Pair(0, -1),  Pair(0, 0),  Pair(0, 1),
            Pair(1, -1),  Pair(1, 0),  Pair(1, 1)
        )
        val originalLocation = block.location.clone()

        when (handItem) {
            hoeClass.hoe, hoeClass.durableHoe -> {
                convertToFarmland(block)
            }

            hoeClass.lightAndSturdyHoe -> {
                for ((i, j) in offsets) {
                    val targetLocation = originalLocation.clone().add(i.toDouble(), 0.0, j.toDouble())
                    if (targetLocation.block.type == Material.DIRT) {
                        convertToFarmland(targetLocation.block)
                    }
                }
            }

            hoeClass.autoHoe -> {
                val offHandItem = inventory.itemInOffHand
                val plant = plants.find { it.getSeedItem().itemMeta == offHandItem.itemMeta } ?: return

                for ((i, j) in offsets) {
                    val newPlant = plant.copy()
                    val targetLocation = originalLocation.clone().add(i.toDouble(), 0.0, j.toDouble())
                    val targetBlock = targetLocation.block
                    if (targetBlock.type == Material.DIRT) {
                        convertToFarmland(targetBlock)
                        if (offHandItem.amount > 0) {
                            plant(targetBlock, newPlant)
                            offHandItem.amount--
                        }
                    }
                }
            }
        }
    }

    override fun Player.watering(block: Block) {
        val handItem = inventory.itemInMainHand
        val wateringCans = wateringCanClass.wateringCans

        if (handItem !in wateringCans) return

        fun updateFarmland(targetBlock: Block) {
            val farmland = targetBlock.blockData as? Farmland
            if (farmland != null && farmland.moisture < farmland.maximumMoisture) {
                farmland.moisture = farmland.maximumMoisture
                targetBlock.blockData = farmland
                gameData.interactionFarmlands.add(targetBlock)
            }
        }

        updateFarmland(block)
        updateFarmland(block.getRelative(BlockFace.DOWN))
    }

    override fun Player.plant(block: Block, plant: Plant) {
        val item = inventory.itemInMainHand
        val cropBlock = block.getRelative(BlockFace.UP)

        if (block.blockData !is Farmland) return
        if (gameData.blockToPlantMap[block] is Plant) return

        if (Random.nextInt(100) < 15) {
            plant.isWeeds = true
        }

        cropBlock.type = Material.WHEAT
        val cropBlockData = cropBlock.blockData
        if (cropBlockData is Ageable) {
            cropBlockData.age = 0
            cropBlock.blockData = cropBlockData
        }

        // 블럭 == 심었을 때 시각적으로 보이는 작물 즉, 씨앗의 블럭
        plant.block = cropBlock
        plant.isPlant = true

        if (item.itemMeta == plant.getSeedItem().itemMeta) item.amount--
        gameData.plantList.add(plant)

        // 블럭 == 작물이 심어진 경작지.
        gameData.blockToPlantMap[block] = plant
    }

    override fun Player.harvesting(plant: Plant) {
        if (!plant.isPlant) return
        val block = plant.block ?: return // 식물 블럭
        val cropBlock = block.getRelative(BlockFace.DOWN) // 경작지

        if (plant.name == DeadGrassPlant().name) {
            removePlant(plant, cropBlock)
            cropBlock.type = Material.AIR
            return
        }
        if (!plant.isHarvestComplete) return

        val harvestItem = plant.getHarvestItem()
        val isNutrient = (plant.capsuleType == CapsuleType.Nutrient)
        val iridiumChance = if (isNutrient) 0 else 30
        val goldChance = if (isNutrient) 30 else 70

        if (plant.yield != 1) {
            val yieldCount = Random.nextInt(1, plant.yield + 1)
            repeat(yieldCount) {
                val starChance = Random.nextInt(1, 101)
                when {
                    starChance <= iridiumChance -> {
                        // TODO: 이리듐 관련 모델 데이터 수정
                    }
                    starChance <= iridiumChance + goldChance -> {
                        // TODO: 금 관련 모델 데이터 수정
                    }
                    else -> {
                        // TODO: 은 관련 모델 데이터 수정 (기본)
                    }
                }
                inventory.addItem(harvestItem)
            }
            block.type = Material.AIR
            removePlant(plant, cropBlock)
            return
        }

        block.type = Material.AIR
        inventory.addItem(harvestItem)
        removePlant(plant, cropBlock)
    }

    private fun Player.removePlant(plant: Plant, block: Block) {
        gameData.plantList.remove(plant)
        gameData.blockToPlantMap.remove(block)
    }

    override fun Player.capsule(plant: Plant) {
        val handItem = this.inventory.itemInMainHand
        val offHandItem = this.inventory.itemInOffHand
        val capsuleGun = capsuleClass.capsuleGun
        val capsules = capsuleClass.capsules

        if (handItem != capsuleGun) return
        if (offHandItem !in capsules) return
        if (plant.isHarvestComplete) return
        if (!plant.isPlant) return
        if (plant.capsuleType != CapsuleType.None) return

        when (offHandItem) {
            capsuleClass.growthCapsule -> {
                plant.capsuleType = CapsuleType.Growth
            }
            capsuleClass.nutrientCapsule -> {
                plant.capsuleType = CapsuleType.Nutrient
            }
            capsuleClass.weedKillerCapsule -> {
                plant.capsuleType = CapsuleType.WeedKiller
            }
            else -> {
                plant.capsuleType = CapsuleType.None
            }
        }

        offHandItem.amount -= 1
    }

    override fun Plant.isWatering(): Boolean {
        val block = block
        if (block !is Block) return false
        val cropBlock = block.getRelative(BlockFace.DOWN)
        if (cropBlock.type != Material.FARMLAND) return false
        val farmland = cropBlock.blockData as? Farmland ?: return false

        return farmland.moisture == farmland.maximumMoisture
    }

    override fun Plant.growth() {
        if (!isPlant) return
        val blockBelow = block?.getRelative(BlockFace.DOWN) ?: return
        val farmland = (blockBelow.blockData as? Farmland) ?: return

        if (!plantSeasons.contains(gameData.season)) {
            if (name != DeadGrassPlant().name) {
                wither()
            }
            return
        }

        if (!isWatering() || isHarvestComplete) return

        val world = blockBelow.world
        val location = blockBelow.location
        val baseX = location.blockX
        val baseY = location.blockY
        val baseZ = location.blockZ

        var weedFound = false
        var nonWeedFound = false

        loop@ for (x in (baseX - 1)..(baseX + 1)) {
            for (z in (baseZ - 1)..(baseZ + 1)) {
                val currentBlock = world.getBlockAt(x, baseY, z)
                if (currentBlock.type != Material.FARMLAND) continue
                val adjacentPlant = gameData.blockToPlantMap[currentBlock] ?: continue

                if (adjacentPlant.isWeeds) {
                    weedFound = true
                    break@loop
                } else {
                    nonWeedFound = true
                }
            }
        }

        if (weedFound) {
            weedsCount++
            if (weedsCount > 2) {
                wither()
            }
        } else if (nonWeedFound) {
            if (capsuleType == CapsuleType.Growth) {
                if (harvestCycle > 1) harvestCycle--
                if (harvestCycle <= 1) isHarvestComplete = true
            } else {
                if (harvestCycle > 0) harvestCycle--
                if (harvestCycle <= 0) isHarvestComplete = true
            }
            weedsCount = 0
        }

        farmland.moisture = 0
    }



    private fun Plant.wither() {
        val currentBlock = block ?: return
        val farmland = currentBlock.blockData as? Farmland ?: return
        val deadGrassPlant = DeadGrassPlant().apply {
            isHarvestComplete = true
            isPlant = true
        }
        val cropBlock = currentBlock.getRelative(BlockFace.UP)

        cropBlock.type = Material.WHEAT_SEEDS
        gameData.plantList.remove(this)
        gameData.plantList.add(deadGrassPlant)
        gameData.blockToPlantMap[currentBlock] = deadGrassPlant
        currentBlock.type = Material.DIRT
    }
}