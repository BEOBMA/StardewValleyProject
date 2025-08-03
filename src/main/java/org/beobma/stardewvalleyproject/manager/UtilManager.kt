package org.beobma.stardewvalleyproject.manager

import net.kyori.adventure.text.minimessage.MiniMessage
import org.beobma.stardewvalleyproject.manager.DataManager.playerList

object UtilManager {
    val miniMessage = MiniMessage.miniMessage()

    fun isSingle(): Boolean {
        return playerList.size == 1
    }
}