package org.beobma.stardewvalleyproject.listener

import org.beobma.stardewvalleyproject.manager.MineManager.gatheringPlayers
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemHeldEvent

class OnPlayerItemHeld : Listener {
    @EventHandler
    fun onHotbarSlotChange(event: PlayerItemHeldEvent) {
        val player = event.player

        if (gatheringPlayers.contains(player.uniqueId)) {
            event.isCancelled = true
            return
        }
    }
}