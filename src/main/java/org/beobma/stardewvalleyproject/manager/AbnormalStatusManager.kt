package org.beobma.stardewvalleyproject.manager

import org.beobma.stardewvalleyproject.StardewValley
import org.bukkit.entity.Player

interface AbnormalStatusHandler {
    fun Player.faint()
}

class DefaultAbnormalStatusHandler : AbnormalStatusHandler {

    override fun Player.faint() {
        if (player !is Player) return

        StardewValley().loggerMessage("StardewValley ${player!!} is Faint")
        //기절 시 효과
    }
}
class AbnormalStatusManager(private val handler: AbnormalStatusHandler) {

    fun Player.faint() {
        handler.run { this@faint.faint() }
    }
}