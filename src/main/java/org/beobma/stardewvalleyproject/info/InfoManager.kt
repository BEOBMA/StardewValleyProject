package org.beobma.stardewvalleyproject.info

import org.beobma.stardewvalleyproject.farming.Plant
import org.bukkit.entity.Player

class InfoManager {
    companion object {
        val players: MutableList<Player> = mutableListOf()
        var plant: MutableList<Plant> = mutableListOf()
    }
}