package org.beobma.stardewvalleyproject.data

import kotlinx.serialization.Serializable
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.manager.Season
import org.bukkit.block.Block
import org.bukkit.entity.Player

@Serializable
data class GameData(
    val players: MutableList<Player>,
    val plantList: MutableList<Plant>,
    var time: Long,
    var season: Season,
    var day: Int,
    val blockToPlantMap: MutableMap<Block, Plant> = mutableMapOf()
)
