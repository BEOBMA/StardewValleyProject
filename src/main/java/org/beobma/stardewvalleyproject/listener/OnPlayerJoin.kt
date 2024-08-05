package org.beobma.stardewvalleyproject.listener

import org.beobma.stardewvalleyproject.manager.DefaultDataHandler.Companion.gameData
import org.beobma.stardewvalleyproject.manager.DefaultTimeHandler
import org.beobma.stardewvalleyproject.manager.TimeManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class OnPlayerJoin : Listener {
    private val timeManager = TimeManager(DefaultTimeHandler())

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        gameData.players.add(player)
        timeManager.timePlay()
    }
}