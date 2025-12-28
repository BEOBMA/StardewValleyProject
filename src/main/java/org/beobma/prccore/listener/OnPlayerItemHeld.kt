package org.beobma.prccore.listener

import org.beobma.prccore.manager.DataManager.mines
import org.beobma.prccore.manager.MineManager.gatheringPlayers
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


        if (mines.any { it.players.contains(player) }) {
            return
        }
    }
}