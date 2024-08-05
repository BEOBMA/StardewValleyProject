package org.beobma.stardewvalleyproject.data

import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.manager.Season
import org.bukkit.block.Block
import org.bukkit.entity.Player
import java.io.Serializable

data class GameData(
    var hour: Int,
    var minute: Int,
    var season: Season,
    var day: Int,
    val players: MutableList<Player>,
    val plantList: MutableList<Plant>,
    val blockToPlantMap: MutableMap<Block, Plant> = mutableMapOf()
) : Serializable
