package org.beobma.stardewvalleyproject.event

import org.beobma.stardewvalleyproject.data.DataManager.Companion.gameData
import org.beobma.stardewvalleyproject.farming.Plant
import org.beobma.stardewvalleyproject.time.TimeManager
import org.beobma.stardewvalleyproject.tool.WateringCan
import org.beobma.stardewvalleyproject.util.Util
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class EventManager : Listener {
    @EventHandler
    fun onInventoryOpen(event: InventoryOpenEvent) {
        if (!Util().isSingle()) return
        TimeManager().timePause()
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        if (!Util().isSingle()) return
        TimeManager().timePlay()
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        gameData.players.add(player)
        TimeManager().timePlay()
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player

        gameData.players.remove(player)
        if (gameData.players.isEmpty()) {
            TimeManager().timePause()
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val block = event.clickedBlock
        val p3: List<Material> = listOf(Material.WHEAT, Material.CARROT)

        if (block?.type !in p3) return
        if (event.item != WateringCan().wateringCan) return
        var p1 = false
        var p2: Plant? = null
        gameData.plantList.forEach {
            if (it.block == block) {
                p1 = true
                p2 = it
                return@forEach
            }
        }

        if (p1) {
            p2?.water()
        }
    }
}