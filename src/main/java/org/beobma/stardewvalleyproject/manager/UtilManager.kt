package org.beobma.stardewvalleyproject.manager

import org.beobma.stardewvalleyproject.manager.DefaultDataHandler.Companion.gameData

interface UtilHandler {
    fun isSingle(): Boolean
}

class DefaultUtileHandler : UtilHandler {
    override fun isSingle(): Boolean {
        return gameData.players.size == 1
    }
}

object UtilManager {
    private val handler: UtilHandler = DefaultUtileHandler()

    fun isSingle(): Boolean {
        return gameData.players.size == 1
    }
}