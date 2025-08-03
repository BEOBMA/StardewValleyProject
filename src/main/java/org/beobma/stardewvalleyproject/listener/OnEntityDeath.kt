package org.beobma.stardewvalleyproject.listener

import org.beobma.stardewvalleyproject.manager.DataManager.mines
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

        enemy.isDead = true
        enemy.isSpawn = false
    }
}