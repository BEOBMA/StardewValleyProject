package org.beobma.stardewvalleyproject.listener

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

class OnPlayerInteract : Listener {

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.hand != EquipmentSlot.HAND) return
        if (event.action != Action.RIGHT_CLICK_BLOCK) return

        val player = event.player
        val block = event.clickedBlock ?: return
        val item = event.item
        val cmd = item?.getCustomModelData()
        val plantAtBlock = plantList.find { it.farmlandLocation == block.location }

        // 테스트용 시계
        if (item?.type == Material.CLOCK) {
            TimeManager.endOfDay()
            return
        }

        // 광산
        if (mines.any { it.players.contains(player) }) {
            handleMine(player, block)
            event.isCancelled = true
            return
        }

        // 경작
        if (cmd != null && cmd in HOE_CUSTOM_MODEL_DATAS && block.type == Material.DIRT) {
            player.tillage(block)
            event.isCancelled = true
            return
        }

        // 물 주기
        if (cmd != null && cmd in WATERINGCAN_CUSTOM_MODEL_DATAS &&
            (block.type == Material.FARMLAND || block.type == Material.WHEAT)
        ) {
            player.watering(block)
            event.isCancelled = true
            return
        }

        // 식물 상호작용 처리
        plantAtBlock?.let {
            handlePlantInteraction(player, it, item, block)
            event.isCancelled = true
            return
        }

        // 심기
        if (item == null) return
        if (item.type != Material.BLACK_DYE) return

        val registered = getRegisterPlants()
            .find { it.getSeedItem().getCustomModelData() == cmd } ?: run {
            event.isCancelled = true; return
        }

        if (block.type != Material.FARMLAND) { event.isCancelled = true; return }
        if (plantList.any { it.farmlandLocation == block.location }) { event.isCancelled = true; return }

        val newPlant = getPlantInstance(registered)
        player.plant(block, newPlant)
        event.isCancelled = true
    }

    @EventHandler
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        val player = event.player
        val entity = event.rightClicked
        val main = player.inventory.itemInMainHand
        val cmd = main.getCustomModelData()

        // 광산 선택 인터렉션
        if (entity is Interaction) {
            val key = NamespacedKey("module", "mine_interaction")
            val pdc = entity.persistentDataContainer
            if (pdc.getOrDefault(key, PersistentDataType.BOOLEAN, false)) {
                showMineFloorSelector(player)
                return
            }
        }

        if (entity !is ItemDisplay) return
        val plant = plantList.find { it.uuidString == entity.uniqueId.toString() } ?: return
        val farmland = plant.farmlandLocation ?: return

        // 물 주기
        if (cmd in WATERINGCAN_CUSTOM_MODEL_DATAS) {
            player.watering(farmland.block)
            event.isCancelled = true
            return
        }

        // 식물 상호작용 처리
        handlePlantInteraction(player, plant, main, farmland.block)
        event.isCancelled = true
    }

    @EventHandler
    fun onPlayerDamage(event: EntityDamageByEntityEvent) {
        val damager = event.damager as? Player ?: return
        val tool = damager.inventory.itemInMainHand
        val durability = tool.getCurrentCustomDurability() ?: return
        if (durability <= 0) return
        tool.decreaseCustomDurability(1, damager)
    }

    /** 광산 상호작용 처리 */
    private fun handleMine(player: Player, block: Block) {
        val mine = mines.find { it.players.contains(player) } ?: return
        when (block) {
            mine.exitBlockLocation?.block -> player.approach(mine, mine.floor + 1)
            mine.startBlockLocation?.block -> player.approach(mine, mine.floor - 1, true)
            else -> {
                if (player.inventory.itemInMainHand.type != Material.WOODEN_SHOVEL) return
                mines.find { it == mine }?.resources
                    ?.find { it.location.block == block }
                    ?.let { player.gathering(it) }
            }
        }
    }

    /** 식물 상호작용 처리 */
    private fun handlePlantInteraction(player: Player, plant: Plant, item: ItemStack?, block: Block) {
        val status = plant.plantStatus
        if (!status.isPlant) return

        val main = item
        val off = player.inventory.itemInOffHand
        val mainCmd = main?.getCustomModelData()
        val offCmd = off.getCustomModelData()
        val registered = getRegisterPlants()
            .find { it.getSeedItem().getCustomModelData() == plant.getSeedItem().getCustomModelData() }

        // 빈손/무관 도구 상호작용
        if (mainCmd == null) {
            when {
                status.isHarvestComplete -> player.harvesting(plant)
                status.isDeadGrass       -> player.removePlant(plant)
                status.isWeeds           -> {
                    val display = plant.getItemDisplay() ?: return
                    val newStack = display.itemStack.apply {
                        itemMeta = itemMeta.apply { setCustomModelData(plantModels[registered]) }
                    }
                    display.setItemStack(newStack)
                    status.isWeeds = false
                    status.weedsCount = 0
                }
            }
            return
        }

        // 캡슐 상호작용
        if (off.type == Material.SADDLE) {
            val canShoot = mainCmd == CAPSULEGUN_CUSTOM_MODEL_DATA &&
                    status.capsuleType == CapsuleType.None &&
                    offCmd in CAPSULE_MODEL_DATAS
            if (canShoot) player.capsule(plant)
            return
        }

        // 관개 상호작용
        if (main.type == Material.WOODEN_SHOVEL) {
            if (mainCmd in WATERINGCAN_CUSTOM_MODEL_DATAS && !plant.isWatering()) {
                player.watering(block)
            }
            return
        }
    }
}