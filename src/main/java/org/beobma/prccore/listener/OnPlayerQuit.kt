package org.beobma.prccore.listener

import org.beobma.prccore.manager.DataManager.mines
import org.beobma.prccore.manager.DataManager.playerList
import org.beobma.prccore.manager.MineManager.leaveMine
import org.beobma.prccore.manager.TimeManager.timePause
import org.beobma.prccore.manager.TimeManager.unShowTimeBossBar
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
        unShowTimeBossBar(player)
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