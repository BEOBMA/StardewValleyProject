package org.beobma.stardewvalleyproject.manager

import org.beobma.stardewvalleyproject.manager.DataManager.gameData

interface UtilHandler {
    fun isSingle(): Boolean
}

object UtilManager : UtilHandler {
    override fun isSingle(): Boolean {
        return gameData.players.size == 1
    }
}