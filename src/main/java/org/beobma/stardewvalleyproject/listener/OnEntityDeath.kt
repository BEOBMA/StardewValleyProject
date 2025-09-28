package org.beobma.stardewvalleyproject.listener

import kr.eme.semiMission.objects.events.MissionEvent
import org.beobma.stardewvalleyproject.manager.DataManager.mines
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class OnEntityDeath : Listener {
    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        val entity = event.entity
        val player = event.entity.killer ?: return
        val mine = mines.find { it.players.contains(player) } ?: return
        val enemy = mine.enemys.find { it.enemyUUID == entity.uniqueId.toString() } ?: return

        Bukkit.getPluginManager().callEvent(
            MissionEvent(player, "PLAYER_PROGRESS", "mine_module", 1)
        )


        enemy.isDead = true
        enemy.isSpawn = false
    }
}