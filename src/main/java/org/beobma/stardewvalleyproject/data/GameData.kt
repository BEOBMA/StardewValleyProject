package org.beobma.stardewvalleyproject.data

import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.block.Block
import org.bukkit.entity.Player
import java.io.Serializable

data class GameData(
    var hour: Int,
    var minute: Int,
    var season: Season,
    var day: Int,
    val players: HashSet<Player>,
    val plantList: HashSet<Plant>,
    val blockToPlantMap: HashMap<Block, Plant> = hashMapOf(),
    val interactionFarmlands: HashSet<Block> = hashSetOf()
) : Serializable
