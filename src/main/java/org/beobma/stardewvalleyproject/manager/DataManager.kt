package org.beobma.stardewvalleyproject.manager

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.beobma.stardewvalleyproject.StardewValley
import org.beobma.stardewvalleyproject.data.GameData
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

interface DataHandler {
    fun saveData()
    fun loadData()
}


class DefaultDataHandler : DataHandler {
    private val folder: File = File(StardewValley.instance.dataFolder, "/data")
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    companion object {
        var gameData = GameData(6, 0, Season.Spring, 1, mutableListOf(), mutableListOf())
    }

    override fun saveData() {
        logMessage("StardewValley Plugin Data Save")
        try {
            if (!folder.exists()) {
                folder.mkdirs()
            }
            val dataFile = File(folder, "data.json")
            FileWriter(dataFile).use { writer ->
                gson.toJson(gameData, writer)
            }
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
            val dataFile = File(folder, "data.json")
            if (dataFile.exists()) {
                FileReader(dataFile).use { reader ->
                    gameData = gson.fromJson(reader, GameData::class.java)
                }
            }
        } catch (e: IOException) {
            logMessage("Failed to load data: ${e.message}")
        } catch (e: Exception) {
            logMessage("Failed to load data: ${e.message}")
        }
    }

    private fun logMessage(message: String) {
        StardewValley.instance.loggerMessage(message)
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
