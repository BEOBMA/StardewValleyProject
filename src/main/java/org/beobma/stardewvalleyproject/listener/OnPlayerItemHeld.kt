package org.beobma.stardewvalleyproject.listener

import org.beobma.stardewvalleyproject.manager.DataManager.mines
import org.beobma.stardewvalleyproject.manager.MineManager.approach
import org.beobma.stardewvalleyproject.manager.MineManager.gatheringPlayers
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemHeldEvent

class OnPlayerItemHeld : Listener {
    @EventHandler
    fun onHotbarSlotChange(event: PlayerItemHeldEvent) {
        val player = event.player
        val mine = mines.find { it.players.contains(player) }
        if (mine != null) {
            player.approach(mine, mine.floor + 1)
            event.isCancelled = true
            return
        }


        if (gatheringPlayers.contains(player.uniqueId)) {
            event.isCancelled = true
            return
        }
    }
}