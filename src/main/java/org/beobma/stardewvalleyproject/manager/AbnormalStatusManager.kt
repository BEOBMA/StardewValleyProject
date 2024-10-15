package org.beobma.stardewvalleyproject.manager

import org.beobma.stardewvalleyproject.StardewValley
import org.bukkit.entity.Player

interface AbnormalStatusHandler {
    fun Player.faint()
}

object AbnormalStatusManager : AbnormalStatusHandler {

    override fun Player.faint() {
        StardewValley().loggerMessage("StardewValley ${this@faint} is Faint")
        //기절 시 효과
    }
}