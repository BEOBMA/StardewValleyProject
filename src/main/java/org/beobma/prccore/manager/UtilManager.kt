package org.beobma.prccore.manager

import net.kyori.adventure.text.minimessage.MiniMessage
import org.beobma.prccore.manager.DataManager.playerList

object UtilManager {
    val miniMessage = MiniMessage.miniMessage()

    fun isSingle(): Boolean {
        return playerList.size == 1
    }
}