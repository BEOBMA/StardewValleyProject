package org.beobma.stardewvalleyproject.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.beobma.stardewvalleyproject.StardewValley
import org.beobma.stardewvalleyproject.farming.Plant
import org.bukkit.entity.Player
import java.io.File

@Serializable
data class GameData(val players: MutableList<Player>, val plantList: MutableList<Plant>, var time: Long, var season: Season, var day: Int)

class DataManager {
    private val folder: File = File(StardewValley.instance.dataFolder, "/data")
    private val json = Json { prettyPrint = true }

    companion object {
        var gameData = GameData(mutableListOf(), mutableListOf(), 0L, Season.Spring, 1)
    }


    fun saveData() {
        StardewValley().loggerMessage("StardewValley Plugin Data Save")
        val data = json.encodeToString(gameData)

        File(folder, "data.json").writeText(data)
    }

    fun loadData() {
        StardewValley().loggerMessage("StardewValley Plugin Data Load")
        if (!folder.exists()) {
            folder.mkdirs()
            return
        }
        val data = File(folder, "data.json")
        val newGameData = data.readText()
        gameData = json.decodeFromString(newGameData)
    }
}

enum class Season {
    Spring, Summer, Autumn, Winter
}