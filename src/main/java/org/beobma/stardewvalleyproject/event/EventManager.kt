package org.beobma.stardewvalleyproject.event

import org.beobma.stardewvalleyproject.info.InfoManager.Companion.players
import org.beobma.stardewvalleyproject.time.TimeManager
import org.beobma.stardewvalleyproject.util.Util
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
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

        players.add(player)
        TimeManager().timePlay()
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player

        players.remove(player)
        if (players.isEmpty()) {
            TimeManager().timePause()
        }
    }
}