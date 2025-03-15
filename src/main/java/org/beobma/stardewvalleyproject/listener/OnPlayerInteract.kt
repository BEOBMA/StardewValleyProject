package org.beobma.stardewvalleyproject.listener

import org.beobma.stardewvalleyproject.StardewValley
import org.beobma.stardewvalleyproject.manager.DataManager.gameData
import org.beobma.stardewvalleyproject.manager.FarmingManager.capsule
import org.beobma.stardewvalleyproject.manager.FarmingManager.harvesting
import org.beobma.stardewvalleyproject.manager.FarmingManager.isWatering
import org.beobma.stardewvalleyproject.manager.FarmingManager.plant
import org.beobma.stardewvalleyproject.manager.FarmingManager.plants
import org.beobma.stardewvalleyproject.manager.FarmingManager.tillage
import org.beobma.stardewvalleyproject.manager.FarmingManager.watering
import org.beobma.stardewvalleyproject.manager.MineManager.approach
import org.beobma.stardewvalleyproject.manager.MineManager.gathering
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.tool.Capsule
import org.beobma.stardewvalleyproject.tool.CapsuleType
import org.beobma.stardewvalleyproject.tool.Hoe
import org.beobma.stardewvalleyproject.tool.WateringCan
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class OnPlayerInteract : Listener {
    private val wateringCan = WateringCan().wateringCans
    private val hoes = Hoe().hoes
    private val capsuleGun = Capsule().capsuleGun
    private val capsules = Capsule().capsules

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val block = event.clickedBlock ?: return
        val item = event.item ?: return
        val cropBlock = block.getRelative(BlockFace.DOWN)
        val plant = gameData.plantList.find { it.block == block }
        if (!event.action.name.contains("RIGHT")) return

        if (gameData.mines.any { it.players.contains(player) }) {
            handlerMine(player, block)
            return
        }

//        if () {
//            player.approach(null, 1)
//            return
//        }
//       광산 1층 진입 코드

        plant?.let {
            handlePlant(player, it, item, cropBlock)
            event.isCancelled = true
            return
        }

        if (item in hoes && block.type == Material.DIRT) {
            player.tillage(block)
            event.isCancelled = true
            return
        }

        if (item in wateringCan && block.type == Material.FARMLAND) {
            player.watering(block)
            event.isCancelled = true
            return
        }

        if (item.type == Material.WHEAT_SEEDS) {
            val plant = plants.filter { it.getSeedItem().type != Material.AIR } .find { it.getSeedItem().itemMeta.displayName() == item.itemMeta.displayName() } ?: run {
                event.isCancelled = true
                return
            }
            val newPlant = plant.copy()

            if (block.type != Material.FARMLAND) {
                event.isCancelled = true
                return
            }

            if (gameData.blockToPlantMap[block] is Plant) {
                event.isCancelled = true
                return
            }
            player.plant(block, newPlant)
            event.isCancelled = true
            return
        }
    }
    private fun handlerMine(player: Player, block: Block) {
        val mines = gameData.mines
        val mine = mines.find { it.players.contains(player) } ?: return
        val resource = mine.resources.find { it.location == block.location }
        val exitBlock = mine.exitBlock
        val startBlock = mine.startBlock

        if (resource != null) {
            player.gathering(resource)
            return
        }

        if (exitBlock != null && exitBlock == block) {
            player.approach(mine, mine.floor + 1)
            return
        }

        if (startBlock != null && startBlock == block) {
            player.approach(mine, mine.floor - 1)
            return
        }
        return
    }

    private fun handlePlant(player: Player, plant: Plant, item: ItemStack, block: Block) {
        if (!plant.isPlant) return
        when {
            plant.isHarvestComplete -> {
                player.harvesting(plant)
            }
            item in wateringCan && !plant.isWatering() -> {
                player.watering(block)
            }
            item == capsuleGun && plant.capsuleType == CapsuleType.None && capsules.any { it.displayName() == item.displayName() } -> {
                player.capsule(plant)
            }
        }
    }
}