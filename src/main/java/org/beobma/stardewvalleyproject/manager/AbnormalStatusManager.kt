package org.beobma.stardewvalleyproject.manager

import org.beobma.stardewvalleyproject.StardewValley
import org.bukkit.entity.Player

interface AbnormalStatusHandler {
    fun Player.faint()
}

class DefaultAbnormalStatusHandler : AbnormalStatusHandler {

    override fun Player.faint() {
        StardewValley().loggerMessage("StardewValley ${this@faint} is Faint")
        //기절 시 효과
    }
}

class AbnormalStatusManager(private val handler: AbnormalStatusHandler) {

    fun Player.faint() {
        handler.run { this@faint.faint() }
    }
}