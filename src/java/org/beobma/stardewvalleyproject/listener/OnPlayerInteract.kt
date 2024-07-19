@file:Suppress("DEPRECATION")

package org.beobma.stardewvalleyproject.listener

import org.beobma.stardewvalleyproject.manager.DefaultDataHandler.Companion.gameData
import org.beobma.stardewvalleyproject.manager.DefaultPlantHanler
import org.beobma.stardewvalleyproject.manager.PlantManager
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.tool.WateringCan
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class OnPlayerInteract : Listener {

    private val itemMaterialList = listOf(Material.WHEAT_SEEDS)
    private val blockMaterialList = listOf(Material.WHEAT)
    private val plantManager = PlantManager(DefaultPlantHanler())

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val block = event.clickedBlock ?: return
        val item = event.item

        val plant = gameData.blockToPlantMap[block] ?: return

        if (block.type in blockMaterialList && plant.isPlant) {
            when {
                plant.isHarvestComplete -> plantManager.run { plant.harvesting() }
                item?.type == WateringCan().wateringCan.type && !plant.isWater -> plantManager.run { plant.water() }
            }
            return
        }

        if (item !is ItemStack) return
        if (item.type in itemMaterialList && block.type == Material.FARMLAND) {
            when (item.itemMeta.displayName) {
                "예시" -> {
                    plantManager.run {
                        Plant("작물 이름", 0, 100, 0, 0, 0, item, item).plant(block)
                    }
                }
            }
        }
    }
}
