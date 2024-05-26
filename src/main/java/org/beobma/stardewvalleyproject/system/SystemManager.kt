package org.beobma.stardewvalleyproject.system

import org.beobma.stardewvalleyproject.StardewValley
import org.bukkit.entity.Player

class SystemManager {

    fun Player.faint() {
        if (player !is Player) return

        StardewValley().loggerMessage("StardewValley ${player!!} is Faint")
        //기절 시 효과
    }
}