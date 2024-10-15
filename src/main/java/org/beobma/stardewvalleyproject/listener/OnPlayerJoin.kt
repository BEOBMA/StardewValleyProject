package org.beobma.stardewvalleyproject.listener

import org.beobma.stardewvalleyproject.manager.DataManager.gameData
import org.beobma.stardewvalleyproject.manager.TimeManager.timePlay
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class OnPlayerJoin : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        gameData.players.add(player)
        timePlay()
    }
}