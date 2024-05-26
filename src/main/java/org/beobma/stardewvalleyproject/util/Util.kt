package org.beobma.stardewvalleyproject.util

import org.beobma.stardewvalleyproject.info.InfoManager.Companion.players

class Util {

    fun isSingle(): Boolean {
        return players.size == 1
    }
}