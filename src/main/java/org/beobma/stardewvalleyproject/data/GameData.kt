package org.beobma.stardewvalleyproject.data

import kotlinx.serialization.Serializable
import org.beobma.stardewvalleyproject.util.Season

@Serializable
data class GameData(
    var hour: Int,
    var minute: Int,
    var season: Season,
    var day: Int,
    var maxMineFloor: Int = 1
)
