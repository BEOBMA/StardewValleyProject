package org.beobma.stardewvalleyproject.manager

import org.beobma.stardewvalleyproject.manager.CustomModelDataManager.getCustomModelData
import org.beobma.stardewvalleyproject.manager.DataManager.gameData
import org.beobma.stardewvalleyproject.manager.DataManager.interactionFarmlands
import org.beobma.stardewvalleyproject.manager.DataManager.plantList
import org.beobma.stardewvalleyproject.manager.PlantManager.PLANT_STAR_ICON_OFFSET
import org.beobma.stardewvalleyproject.manager.PlantManager.getHarvestItem
import org.beobma.stardewvalleyproject.manager.PlantManager.getItemDisplay
import org.beobma.stardewvalleyproject.manager.PlantManager.getPlantInstance
import org.beobma.stardewvalleyproject.manager.PlantManager.getRegisterPlants
import org.beobma.stardewvalleyproject.manager.PlantManager.getSeedItem
import org.beobma.stardewvalleyproject.manager.PlantManager.plantAgIcons
import org.beobma.stardewvalleyproject.manager.PlantManager.plantModels
import org.beobma.stardewvalleyproject.manager.PlantManager.plantSeedIcons
import org.beobma.stardewvalleyproject.manager.ToolManager.AUTO_HOE_CUSTOM_MODEL_DATA
import org.beobma.stardewvalleyproject.manager.ToolManager.CAPSULEGUN_CUSTOM_MODEL_DATA
import org.beobma.stardewvalleyproject.manager.ToolManager.CAPSULE_MODEL_DATAS
import org.beobma.stardewvalleyproject.manager.ToolManager.DURABLE_HOE_CUSTOM_MODEL_DATA
import org.beobma.stardewvalleyproject.manager.ToolManager.GROWTH_CAPSULE_MODEL_DATA
import org.beobma.stardewvalleyproject.manager.ToolManager.HOE_CUSTOM_MODEL_DATAS
import org.beobma.stardewvalleyproject.manager.ToolManager.LIGHT_AND_STURDY_HOE_CUSTOM_MODEL_DATA
import org.beobma.stardewvalleyproject.manager.ToolManager.NUTRIENT_CAPSULE_MODEL_DATA
import org.beobma.stardewvalleyproject.manager.ToolManager.PUMP_WATERINGCAN_CUSTOM_MODEL_DATA
import org.beobma.stardewvalleyproject.manager.ToolManager.WATERINGCAN_CUSTOM_MODEL_DATA
import org.beobma.stardewvalleyproject.manager.ToolManager.WATERINGCAN_CUSTOM_MODEL_DATAS
import org.beobma.stardewvalleyproject.manager.ToolManager.WEED_KILLER_CAPSULE_MODEL_DATA
import org.beobma.stardewvalleyproject.manager.ToolManager.decreaseCustomDurability
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.plant.list.DeadGrassPlant
import org.beobma.stardewvalleyproject.tool.CapsuleType
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.Farmland
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.random.Random


object FarmingManager {
    // 3x3 크기
    private val NEIGHBOR_OFFSETS: List<Pair<Int, Int>> = listOf(
        -1 to -1, -1 to 0, -1 to 1,
        0 to -1, 0 to 0, 0 to 1,
        1 to -1, 1 to 0, 1 to 1
    )

    fun Player.tillage(block: Block) {
        val mainHandItem = inventory.itemInMainHand
        val customModelData = mainHandItem.getCustomModelData()

        if (block.type != Material.DIRT) return
        if (customModelData !in HOE_CUSTOM_MODEL_DATAS) return

        when (customModelData) {
            DURABLE_HOE_CUSTOM_MODEL_DATA -> durableHoeHandler(block)

            LIGHT_AND_STURDY_HOE_CUSTOM_MODEL_DATA -> lightAndSturdyHoeHandler(block)

            AUTO_HOE_CUSTOM_MODEL_DATA -> autoHoeHandelr(block, this)
        }

        mainHandItem.decreaseCustomDurability(1, this)
    }

    private fun convertToFarmland(block: Block) {
        block.type = Material.FARMLAND
        interactionFarmlands.add(block.location)
    }

    private fun durableHoeHandler(block: Block) {
        convertToFarmland(block)
    }

    private fun lightAndSturdyHoeHandler(block: Block) {
        val origin = block.location.clone()
        for ((dx, dz) in NEIGHBOR_OFFSETS) {
            val targetBlock = origin.clone().add(dx.toDouble(), 0.0, dz.toDouble()).block
            if (targetBlock.type == Material.DIRT) {
                convertToFarmland(targetBlock)
            }
        }
    }

    private fun autoHoeHandelr(block: Block, player: Player) {
        val registeredPlants = getRegisterPlants()
        val origin = block.location.clone()
        val offHandItem = player.inventory.itemInOffHand
        val offHandCustomModelData = offHandItem.getCustomModelData()
        val plantType = registeredPlants.find { it.getSeedItem().getCustomModelData() == offHandCustomModelData }

        for ((dx, dz) in NEIGHBOR_OFFSETS) {
            val targetBlock = origin.clone().add(dx.toDouble(), 0.0, dz.toDouble()).block
            if (targetBlock.type == Material.DIRT) {
                convertToFarmland(targetBlock)

                if (plantType != null && offHandItem.amount > 0) {
                    val plantInstance = getPlantInstance(plantType)
                    player.plant(targetBlock, plantInstance)
                    offHandItem.amount--
                }
            }
        }
    }

    private fun updateFarmland(targetBlock: Block) {
        val farmland = targetBlock.blockData as? Farmland ?: return
        if (farmland.moisture < farmland.maximumMoisture) {
            farmland.moisture = farmland.maximumMoisture
            targetBlock.blockData = farmland
            interactionFarmlands.add(targetBlock.location)
        }
    }


    fun Player.watering(block: Block) {
        val handItem = inventory.itemInMainHand
        val customModelData = handItem.getCustomModelData()

        if (handItem.type != Material.WOODEN_SHOVEL) return
        if (customModelData !in WATERINGCAN_CUSTOM_MODEL_DATAS) return

        when (customModelData) {
            WATERINGCAN_CUSTOM_MODEL_DATA -> wateringCanHandler(block)

            PUMP_WATERINGCAN_CUSTOM_MODEL_DATA -> pumpWateringCanHandler(block)
        }
        playSound(block.location, Sound.ITEM_BUCKET_EMPTY, 1.0f, 1.0f)

        handItem.decreaseCustomDurability(1, this)
    }

    private fun Player.wateringCanHandler(block: Block) {
        spawnParticle(Particle.FALLING_WATER, block.location.add(0.5, 0.5, 0.5), 10, 0.1, 0.1, 0.1, 1.0)
        if (block.type == Material.FARMLAND) updateFarmland(block)
        else updateFarmland(block.getRelative(BlockFace.DOWN))
    }

    private fun Player.pumpWateringCanHandler(block: Block) {
        val origin = block.location.clone()

        for ((dx, dz) in NEIGHBOR_OFFSETS) {
            val targetBlock = origin.clone().add(dx.toDouble(), 0.0, dz.toDouble()).block
            spawnParticle(Particle.FALLING_WATER, targetBlock.location.add(0.5, 0.5, 0.5), 10, 0.1, 0.1, 0.1, 1.0)

            if (targetBlock.type == Material.FARMLAND) updateFarmland(targetBlock)
            else updateFarmland(targetBlock.getRelative(BlockFace.DOWN))
        }
    }

    fun Player.plant(block: Block, plant: Plant) {
        val plantStatus = plant.plantStatus

        var item = inventory.itemInMainHand
        var itemCustomModelData = item.getCustomModelData()
        var registeredPlant = getRegisterPlants().find { it.getSeedItem().getCustomModelData() == itemCustomModelData }

        if (registeredPlant == null) {
            item = inventory.itemInOffHand
            itemCustomModelData = item.getCustomModelData()
            registeredPlant = getRegisterPlants().find { it.getSeedItem().getCustomModelData() == itemCustomModelData }
        }

        if (registeredPlant == null) return

        if (plantList.any { it.farmlandLocation == block.location }) return
        if (Random.nextInt(100) < 15) {
            plantStatus.isWeeds = true
        }

        plant.farmlandLocation = block.location
        plantStatus.isPlant = true

        if (itemCustomModelData == plantSeedIcons[registeredPlant]) item.amount--
        plantList.add(plant)
        val itemDisplay = world.spawn(block.location.add(0.5, 1.4, 0.5), ItemDisplay::class.java)
        val uuidString = itemDisplay.uniqueId.toString()
        plant.uuidString = uuidString
        val itemStack = ItemStack(Material.BLUE_DYE).apply {
            itemMeta = itemMeta.apply {
                if (plantStatus.isWeeds) {
                    setCustomModelData(41)
                }
                else {
                    setCustomModelData(plantModels[registeredPlant])
                }
            }
        }
        itemDisplay.setItemStack(itemStack)
        playSound(block.location, Sound.ITEM_HOE_TILL, 1.0f, 1.0f)
    }

    fun Player.harvesting(plant: Plant) {
        val plantStatus = plant.plantStatus

        if (plantStatus.isDeadGrass) {
            removePlant(plant)
            return
        }
        if (!plantStatus.isHarvestComplete) return
        if (!plantStatus.isPlant) return

        val registeredPlant =
            getRegisterPlants().find { it.getSeedItem().getCustomModelData() == plant.getSeedItem().getCustomModelData() }
        val customModelData = plantAgIcons[registeredPlant] ?: return
        val isNutrient = (plantStatus.capsuleType == CapsuleType.Nutrient)
        val iridiumChance = if (!isNutrient) 0 else 30
        val goldChance = if (isNutrient) 30 else 70
        if (plant.harvestAmount != 1) {
            val yieldCount = Random.nextInt(1, plant.harvestAmount + 1)
            repeat(yieldCount) {
                val harvestItem = plant.getHarvestItem()
                val starChance = Random.nextInt(1, 101)
                when {
                    starChance <= iridiumChance -> {
                        harvestItem.itemMeta = harvestItem.itemMeta.apply { setCustomModelData(customModelData + (PLANT_STAR_ICON_OFFSET * 2)) }
                    }

                    starChance <= iridiumChance + goldChance -> {
                        harvestItem.itemMeta = harvestItem.itemMeta.apply { setCustomModelData(customModelData + (PLANT_STAR_ICON_OFFSET)) }
                    }

                    else -> {
                        harvestItem.itemMeta = harvestItem.itemMeta.apply { setCustomModelData(customModelData) }
                    }
                }
                inventory.addItem(harvestItem)
            }
            removePlant(plant)
            return
        }

        val harvestItem = plant.getHarvestItem()
        val starChance = Random.nextInt(1, 101)
        when {
            starChance <= iridiumChance -> {
                harvestItem.itemMeta = harvestItem.itemMeta.apply { setCustomModelData(customModelData + (PLANT_STAR_ICON_OFFSET * 2)) }
            }

            starChance <= iridiumChance + goldChance -> {
                harvestItem.itemMeta = harvestItem.itemMeta.apply { setCustomModelData(customModelData + (PLANT_STAR_ICON_OFFSET)) }
            }

            else -> {
                harvestItem.itemMeta = harvestItem.itemMeta.apply { setCustomModelData(customModelData) }
            }
        }
        inventory.addItem(harvestItem)
        removePlant(plant)
    }

    fun Player.capsule(plant: Plant) {
        val plantStatus = plant.plantStatus
        val handItem = this.inventory.itemInMainHand
        val handItemCustomModelData = handItem.getCustomModelData()
        val offHandItem = this.inventory.itemInOffHand
        val offItemCustomModelData = offHandItem.getCustomModelData()

        if (handItemCustomModelData != CAPSULEGUN_CUSTOM_MODEL_DATA) return
        if (offItemCustomModelData !in CAPSULE_MODEL_DATAS) return
        if (plantStatus.isHarvestComplete) return
        if (!plantStatus.isPlant) return
        if (plantStatus.capsuleType != CapsuleType.None) return

        when (offItemCustomModelData) {
            GROWTH_CAPSULE_MODEL_DATA -> {
                plantStatus.capsuleType = CapsuleType.Growth
            }

            NUTRIENT_CAPSULE_MODEL_DATA -> {
                plantStatus.capsuleType = CapsuleType.Nutrient
            }

            WEED_KILLER_CAPSULE_MODEL_DATA -> {
                plantStatus.capsuleType = CapsuleType.WeedKiller
            }

            else -> {
                plantStatus.capsuleType = CapsuleType.None
            }
        }
        val farmlandLocation = plant.farmlandLocation
        if (farmlandLocation != null) {
            spawnParticle(Particle.END_ROD, farmlandLocation, 10, 0.0, 0.0, 0.0, 0.0)
            playSound(farmlandLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)
        }
        offHandItem.amount -= 1
    }

    fun Plant.isWatering(): Boolean {
        val farmland = farmlandLocation?.block?.blockData as? Farmland ?: return false
        return farmland.moisture == farmland.maximumMoisture
    }

    fun Plant.growth() {
        val plantStatus = plantStatus

        if (!plantStatus.isPlant) return
        val farmlandBlock = farmlandLocation ?: return
        val farmland = farmlandBlock.block.blockData as? Farmland ?: return

        // 계절 시스템
//        if (!growableSeasons.contains(gameData.season)) {
//            if (this !is DeadGrassPlant) {
//                wither()
//                return
//            }
//            return
//        }

        if (plantStatus.isHarvestComplete) return

        val world = farmlandBlock.world
        val location = farmlandBlock
        val baseX = location.blockX
        val baseY = location.blockY
        val baseZ = location.blockZ

        var weedFound = false
        var nonWeedFound = false

        if (plantStatus.capsuleType != CapsuleType.WeedKiller) {
            loop@ for (x in (baseX - 1)..(baseX + 1)) {
                for (z in (baseZ - 1)..(baseZ + 1)) {
                    val currentBlock = world.getBlockAt(x, baseY, z)
                    if (currentBlock.type != Material.FARMLAND) continue
                    val adjacentPlant = plantList.find { it.farmlandLocation?.block == currentBlock } ?: continue

                    if (adjacentPlant.plantStatus.isWeeds) {
                        weedFound = true
                        break@loop
                    } else {
                        nonWeedFound = true
                    }
                }
            }
            when {
                weedFound -> {
                    plantStatus.weedsCount++
                    if (plantStatus.weedsCount > 2) wither()
                }

                nonWeedFound -> {
                    if (!isWatering()) return
                    val growthDone = if (plantStatus.capsuleType == CapsuleType.Growth) {
                        --remainingGrowthDays <= 1
                    } else {
                        --remainingGrowthDays <= 0
                    }
                    if (growthDone) plantStatus.isHarvestComplete = true
                    val progress = (growthDays - remainingGrowthDays).toDouble() / growthDays
                    val registeredPlant =
                        getRegisterPlants().find { it.getSeedItem().getCustomModelData() == getSeedItem().getCustomModelData() }
                    val itemDisplay = getItemDisplay() ?: return
                    val modelData = plantModels[registeredPlant] ?: return

                    val stage = when {
                        plantStatus.isHarvestComplete -> 3
                        progress >= 0.66 -> 2
                        progress >= 0.33 -> 1
                        else -> 0
                    }
                    itemDisplay.setItemStack(itemDisplay.itemStack.apply { itemMeta = itemMeta.apply { setCustomModelData(modelData + stage) } })


                    plantStatus.weedsCount = 0
                }
            }
        }
        else {
            if (!isWatering()) return
            val growthDone = if (plantStatus.capsuleType == CapsuleType.Growth) {
                --remainingGrowthDays <= 1
            } else {
                --remainingGrowthDays <= 0
            }
            if (growthDone) plantStatus.isHarvestComplete = true
            val progress = (growthDays - remainingGrowthDays).toDouble() / growthDays
            val registeredPlant =
                getRegisterPlants().find { it.getSeedItem().getCustomModelData() == getSeedItem().getCustomModelData() }
            val itemDisplay = getItemDisplay() ?: return
            val modelData = plantModels[registeredPlant] ?: return

            val stage = when {
                plantStatus.isHarvestComplete -> 3
                progress >= 0.66 -> 2
                progress >= 0.33 -> 1
                else -> 0
            }
            itemDisplay.setItemStack(itemDisplay.itemStack.apply { itemMeta = itemMeta.apply { setCustomModelData(modelData + stage) } })


            plantStatus.weedsCount = 0
        }

        farmland.moisture = 0
    }


    fun Player.removePlant(plant: Plant) {
        val farmlandLocation = plant.farmlandLocation
        if (farmlandLocation != null) {
            playSound(farmlandLocation, Sound.ITEM_HOE_TILL, 1.0f, 1.0f)
        }
        plantList.remove(plant)
        val itemDisplay = plant.getItemDisplay() ?: return
        itemDisplay.remove()
    }

    private fun Plant.wither() {
        plantStatus.isDeadGrass = true
        val itemDisplay = getItemDisplay() ?: return
        val itemStack = ItemStack(Material.BLUE_DYE).apply {
            itemMeta = itemMeta.apply {
                setCustomModelData(42)
            }
        }
        itemDisplay.setItemStack(itemStack)
    }
}