package org.beobma.stardewvalleyproject.listener

import org.beobma.stardewvalleyproject.manager.DefaultDataHandler.Companion.gameData
import org.beobma.stardewvalleyproject.manager.DefaultPlantHanler
import org.beobma.stardewvalleyproject.manager.PlantManager
import org.beobma.stardewvalleyproject.manager.PlantManager.getRegisterPlantList
import org.beobma.stardewvalleyproject.manager.PlantManager.harvesting
import org.beobma.stardewvalleyproject.manager.PlantManager.plant
import org.beobma.stardewvalleyproject.manager.PlantManager.water
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.tool.WateringCan
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class OnPlayerInteract : Listener {
    private val wateringCan = WateringCan()

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val block = event.clickedBlock ?: return
        val item = event.item ?: return
        val plant = gameData.blockToPlantMap[block]

        plant?.let {
            handlePlant(player, it, item)
            event.isCancelled = true
            return
        }

        val newPlant = getRegisterPlantList().find { it.getSeedItem().displayName() == item.displayName() }
        newPlant?.let {
            it.plant(block)
            item.amount -= 1
            event.isCancelled = true
            return
        }
    }

    private fun handlePlant(player: Player, plant: Plant, item: ItemStack) {
        if (!plant.isPlant) return
        when {
            plant.isHarvestComplete -> plant.harvesting(player)
            item.isWateringCan() && !plant.isWater -> plant.water()
        }
    }

    private fun ItemStack.isWateringCan(): Boolean {
        return wateringCan.run { this@isWateringCan.isWateringCan() }
    }
}