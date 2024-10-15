package org.beobma.stardewvalleyproject.listener

import org.beobma.stardewvalleyproject.manager.DataManager.gameData
import org.beobma.stardewvalleyproject.manager.TimeManager.timePause
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class OnPlayerQuit : Listener {
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player

        gameData.players.remove(player)
        if (gameData.players.isEmpty()) {
            timePause()
        }
    }
}