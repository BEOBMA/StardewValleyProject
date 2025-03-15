package org.beobma.stardewvalleyproject.manager

import org.beobma.stardewvalleyproject.manager.DataManager.gameData
import org.bukkit.Bukkit

interface UtilHandler {
    fun isSingle(): Boolean
}

object UtilManager : UtilHandler {
    val world = Bukkit.getWorlds().first()
    override fun isSingle(): Boolean {
        return gameData.players.size == 1
    }
}