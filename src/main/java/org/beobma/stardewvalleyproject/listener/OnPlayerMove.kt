package org.beobma.stardewvalleyproject.listener

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import org.beobma.stardewvalleyproject.manager.MineManager.gatheringPlayers
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class OnPlayerMove : Listener {
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        if (player.uniqueId !in gatheringPlayers) return

        val from = event.from
        val to = event.to

        val positionChanged = from.blockX != to.blockX || from.blockY != to.blockY || from.blockZ != to.blockZ
        val yawChanged = from.yaw != to.yaw
        val pitchChanged = from.pitch != to.pitch

        if (positionChanged || yawChanged || pitchChanged) {
            event.to = from
        }
    }

    @EventHandler
    fun onPlayerJump(event: PlayerJumpEvent) {
        val player = event.player

        if (player.uniqueId !in gatheringPlayers) return
        event.isCancelled = true
    }
}