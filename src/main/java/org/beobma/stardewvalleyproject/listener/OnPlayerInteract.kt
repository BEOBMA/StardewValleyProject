package org.beobma.stardewvalleyproject.listener

import org.beobma.stardewvalleyproject.StardewValley
import org.beobma.stardewvalleyproject.manager.CustomModelDataManager.getCustomModelData
import org.beobma.stardewvalleyproject.manager.DataManager.mines
import org.beobma.stardewvalleyproject.manager.DataManager.plantList
import org.beobma.stardewvalleyproject.manager.FarmingManager.capsule
import org.beobma.stardewvalleyproject.manager.FarmingManager.harvesting
import org.beobma.stardewvalleyproject.manager.FarmingManager.isWatering
import org.beobma.stardewvalleyproject.manager.FarmingManager.plant
import org.beobma.stardewvalleyproject.manager.FarmingManager.removePlant
import org.beobma.stardewvalleyproject.manager.FarmingManager.tillage
import org.beobma.stardewvalleyproject.manager.FarmingManager.watering
import org.beobma.stardewvalleyproject.manager.MineManager
import org.beobma.stardewvalleyproject.manager.MineManager.approach
import org.beobma.stardewvalleyproject.manager.MineManager.gathering
import org.beobma.stardewvalleyproject.manager.MineManager.showMineFloorSelector
import org.beobma.stardewvalleyproject.manager.PlantManager.getItemDisplay
import org.beobma.stardewvalleyproject.manager.PlantManager.getPlantInstance
import org.beobma.stardewvalleyproject.manager.PlantManager.getRegisterPlants
import org.beobma.stardewvalleyproject.manager.PlantManager.getSeedItem
import org.beobma.stardewvalleyproject.manager.PlantManager.plantModels
import org.beobma.stardewvalleyproject.manager.TimeManager
import org.beobma.stardewvalleyproject.manager.ToolManager.CAPSULEGUN_CUSTOM_MODEL_DATA
import org.beobma.stardewvalleyproject.manager.ToolManager.CAPSULE_MODEL_DATAS
import org.beobma.stardewvalleyproject.manager.ToolManager.HOE_CUSTOM_MODEL_DATAS
import org.beobma.stardewvalleyproject.manager.ToolManager.WATERINGCAN_CUSTOM_MODEL_DATAS
import org.beobma.stardewvalleyproject.manager.ToolManager.decreaseCustomDurability
import org.beobma.stardewvalleyproject.manager.ToolManager.getCurrentCustomDurability
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.tool.CapsuleType
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.entity.Interaction
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.UUID
import kotlin.apply
import kotlin.let
import kotlin.math.max
import kotlin.math.min


class OnPlayerInteract : Listener {
    private val firstClick: MutableMap<UUID, org.bukkit.Location> = mutableMapOf()

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val clickedBlock = event.clickedBlock ?: return
        val item = event.item
        if (event.hand != EquipmentSlot.HAND) return
        if (!event.action.name.contains("RIGHT")) return

        val plant = plantList.find { it.farmlandLocation == clickedBlock.location }
        val customModelData = item?.getCustomModelData()

        if (item?.type == Material.CLOCK) {
            TimeManager.endOfDay()
            return
        }
        // 광산 관련 처리
        if (mines.any { it.players.contains(player) }) {
            handleMine(player, clickedBlock)
            return
        }

        // 경작
        if (customModelData != null && customModelData in HOE_CUSTOM_MODEL_DATAS && clickedBlock.type == Material.DIRT) {
            player.tillage(clickedBlock)
            event.isCancelled = true
            return
        }

        // 물
        if (customModelData != null && customModelData in WATERINGCAN_CUSTOM_MODEL_DATAS && (clickedBlock.type == Material.FARMLAND || clickedBlock.type == Material.WHEAT)) {
            player.watering(clickedBlock)
            event.isCancelled = true
            return
        }


        // 식물 상호작용 처리
        plant?.let {
            handlePlantInteraction(player, it, item, clickedBlock)
            event.isCancelled = true
            return
        }
        if (item == null) return
        if (item.type == Material.BLACK_DYE) {
            val registeredPlant =
                getRegisterPlants().find { it.getSeedItem().getCustomModelData() == item.getCustomModelData() }
            if (registeredPlant == null || clickedBlock.type != Material.FARMLAND) {
                event.isCancelled = true
                return
            }

            // 이미 식물 있음
            if (plantList.any { it.farmlandLocation == clickedBlock.location }) {
                event.isCancelled = true
                return
            }

            val newPlant = getPlantInstance(registeredPlant)
            player.plant(clickedBlock, newPlant)
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        val player = event.player
        val entity = event.rightClicked
        val item = player.inventory.itemInMainHand
        if (entity is Interaction) {
            val pdc = entity.persistentDataContainer
            val key = NamespacedKey("module", "mine_interaction")
            if (pdc.has(key, PersistentDataType.BOOLEAN) &&
                pdc.get(key, PersistentDataType.BOOLEAN) == true) {
                showMineFloorSelector(player)
                return
            }
        }
        if (entity !is ItemDisplay) return
        val plant = plantList.find { it.uuidString == entity.uniqueId.toString() } ?: return
        val farmlandLocation = plant.farmlandLocation ?: return
        val customModelData = item.getCustomModelData()

        // 물
        if (customModelData in WATERINGCAN_CUSTOM_MODEL_DATAS) {
            player.watering(farmlandLocation.block)
            event.isCancelled = true
            return
        }


        // 식물 상호작용 처리
        plant.let {
            handlePlantInteraction(player, it, item, farmlandLocation.block)
            event.isCancelled = true
            return
        }
    }


    @EventHandler
    fun onPlayerDamage(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        val entity = event.entity

        if (damager !is Player) return
        val tool = damager.inventory.itemInMainHand
        val durability = tool.getCurrentCustomDurability()

        if (durability == null) return
        tool.decreaseCustomDurability(1, damager)
    }

    private fun handleMine(player: Player, block: Block) {
        val mine = mines.find { it.players.contains(player) } ?: return

        when (block.location.block) {
            mine.exitBlockLocation?.block -> player.approach(mine, mine.floor + 1)
            mine.startBlockLocation?.block -> player.approach(mine, mine.floor - 1, true)
            else -> {
                if (player.inventory.itemInMainHand.type != Material.WOODEN_SHOVEL) return
                val resource = mine.resources.find { it.location.block == block }
                if (resource != null) {
                    player.gathering(resource)
                }
            }
        }
    }

    private fun handlePlantInteraction(player: Player, plant: Plant, item: ItemStack?, block: Block) {
        val status = plant.plantStatus
        val offHandItem = player.inventory.itemInOffHand
        val offHandCustomModelData = offHandItem.getCustomModelData()
        val customModelData = item?.getCustomModelData()
        val registeredPlant = getRegisterPlants().find { it.getSeedItem().getCustomModelData() == plant.getSeedItem().getCustomModelData() }

        if (!status.isPlant) return
        if (customModelData == null) {
            when {
                status.isHarvestComplete -> player.harvesting(plant)
                status.isDeadGrass -> player.removePlant(plant)
                status.isWeeds -> {
                    val itemDisplay = plant.getItemDisplay() ?: return
                    val newItemStack = itemDisplay.itemStack.apply {
                        itemMeta = itemMeta.apply {
                            setCustomModelData(plantModels[registeredPlant])
                        }
                    }
                    itemDisplay.setItemStack(newItemStack)
                    status.isWeeds = false
                    status.weedsCount = 0
                }
            }
            return
        }

        if (item.type == Material.WOODEN_SHOVEL) {
            if (customModelData !in WATERINGCAN_CUSTOM_MODEL_DATAS || plant.isWatering()) return
            player.watering(block)
            return
        }

        if (item.type == Material.SADDLE) {
            if (customModelData != CAPSULEGUN_CUSTOM_MODEL_DATA || status.capsuleType != CapsuleType.None || offHandCustomModelData !in CAPSULE_MODEL_DATAS) return
            player.capsule(plant)
            return
        }
    }
}
