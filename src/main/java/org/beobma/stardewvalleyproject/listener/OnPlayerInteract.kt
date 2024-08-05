package org.beobma.stardewvalleyproject.listener

import org.beobma.stardewvalleyproject.manager.DefaultDataHandler.Companion.gameData
import org.beobma.stardewvalleyproject.manager.DefaultPlantHanler
import org.beobma.stardewvalleyproject.manager.PlantManager
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.tool.WateringCan
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class OnPlayerInteract : Listener {
    private val plantManager = PlantManager(DefaultPlantHanler())
    private val wateringCan = WateringCan()

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val block = event.clickedBlock ?: return
        val item = event.item ?: return
        val plant = gameData.blockToPlantMap[block]

        plant?.let {
            handlePlant(player, it, item)
            return
        }

        val seedItem = plantManager.getRegisterPlantList().find { it.seedItem == item }
        seedItem?.let {
            plantManager.run { it.plant(block) }
        }
    }

    private fun handlePlant(player: Player, plant: Plant, item: ItemStack) {
        if (!plant.isPlant) return

        plantManager.run {
            when {
                plant.isHarvestComplete -> plant.harvesting(player)
                item.isWateringCan() && !plant.isWater -> plant.water()
            }
        }
    }

    private fun ItemStack.isWateringCan(): Boolean {
        return wateringCan.run { this@isWateringCan.isWateringCan() }
    }
}