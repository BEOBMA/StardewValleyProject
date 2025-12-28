package org.beobma.prccore.data

import kotlinx.serialization.Serializable
import org.beobma.prccore.util.Season

@Serializable
data class GameData(
    var hour: Int,
    var minute: Int,
    var season: Season,
    var day: Int,
    var maxMineFloor: Int = 1
)
