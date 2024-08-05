package org.beobma.stardewvalleyproject.manager

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import org.beobma.stardewvalleyproject.StardewValley
import org.beobma.stardewvalleyproject.data.GameData
import org.beobma.stardewvalleyproject.serializer.ItemStackSerializer
import java.io.File
import java.io.IOException

interface DataHandler {
    fun saveData()
    fun loadData()
}

class DefaultDataHandler : DataHandler {
    private val folder: File = File(StardewValley.instance.dataFolder, "/data")
    private val json = Json {
        prettyPrint = true
        serializersModule = SerializersModule {
            contextual(ItemStackSerializer())
        }
    }

    companion object {
        var gameData = GameData(mutableListOf(), mutableListOf(), 0L, Season.Spring, 1)
    }

    override fun saveData() {
        logMessage("StardewValley Plugin Data Save")
        try {
            if (!folder.exists()) {
                folder.mkdirs()
            }
            val data = json.encodeToString(gameData)
            File(folder, "data.json").writeText(data)
        } catch (e: IOException) {
            logMessage("Failed to save data: ${e.message}")
        }
    }

    override fun loadData() {
        logMessage("StardewValley Plugin Data Load")
        if (!folder.exists()) {
            folder.mkdirs()
            return
        }
        try {
            val data = File(folder, "data.json")
            if (data.exists()) {
                val newGameData = data.readText()
                gameData = json.decodeFromString(newGameData)
            }
        } catch (e: IOException) {
            logMessage("Failed to load data: ${e.message}")
        }
    }

    private fun logMessage(message: String) {
        StardewValley().loggerMessage(message)
    }
}


class DataManager(private val handler: DataHandler) {
    fun saveData() {
        handler.saveData()
    }

    fun loadData() {
        handler.loadData()
    }
}

enum class Season {
    Spring, Summer, Autumn, Winter
}
