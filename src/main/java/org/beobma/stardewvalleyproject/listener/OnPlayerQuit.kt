package org.beobma.stardewvalleyproject.listener

import org.beobma.stardewvalleyproject.manager.DataManager.mines
import org.beobma.stardewvalleyproject.manager.DataManager.playerList
import org.beobma.stardewvalleyproject.manager.MineManager.leaveMine
import org.beobma.stardewvalleyproject.manager.TimeManager.timePause
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class OnPlayerQuit : Listener {
    private val world = Bukkit.getWorlds().first()

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        val currentMine = mines.find { it.players.contains(player) }
        playerList.remove(player)
        if (currentMine != null) {
            currentMine.players.remove(player)
            player.teleport(Location(world, -191.0, -56.0, 95.0))
            player.leaveMine(currentMine)
        }

        if (playerList.isEmpty()) {
            timePause()
        }
    }
}