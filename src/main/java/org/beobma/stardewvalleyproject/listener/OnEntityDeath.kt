package org.beobma.stardewvalleyproject.listener

import kr.eme.semiMission.api.events.MissionEvent
import kr.eme.semiMission.enums.MissionVersion
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
            MissionEvent(player, MissionVersion.V2,"PLAYER_PROGRESS", "mine_module", 1)
        )


        enemy.isDead = true
        enemy.isSpawn = false
    }
}