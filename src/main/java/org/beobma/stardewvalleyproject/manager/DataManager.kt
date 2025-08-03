package org.beobma.stardewvalleyproject.manager

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.beobma.stardewvalleyproject.StardewValley
import org.beobma.stardewvalleyproject.data.GameData
import org.beobma.stardewvalleyproject.data.LocationListSerializer
import org.beobma.stardewvalleyproject.mine.Mine
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.Location
import org.bukkit.entity.Player
import java.io.File

object DataManager {
    private val dataFolder = File(StardewValley.instance.dataFolder, "data")
    private val json = Json { prettyPrint = true; encodeDefaults = true; ignoreUnknownKeys = true }

    var gameData: GameData = defaultGameMeta()
    var mines: MutableList<Mine> = mutableListOf()
    var plantList: MutableList<Plant> = mutableListOf()
    var interactionFarmlands: MutableList<Location> = mutableListOf()
    var playerList: MutableList<Player> = mutableListOf()

    fun saveAll() {
        save("data.json", gameData)
        save("mines.json", mines)
        save("plants.json", plantList)
        save("interaction_locs.json", interactionFarmlands, LocationListSerializer)
    }

    fun loadAll() {
        gameData = load("data.json") ?: defaultGameMeta()
        mines = load("mines.json") ?: mutableListOf()
        plantList = load("plants.json") ?: mutableListOf()
        interactionFarmlands = (load("interaction_locs.json", LocationListSerializer) ?: mutableListOf()).toMutableList()
    }

    private inline fun <reified T> save(fileName: String, data: T, serializer: KSerializer<T>? = null) {
        val file = File(dataFolder, fileName)
        if (!dataFolder.exists()) dataFolder.mkdirs()
        val start = System.currentTimeMillis()
        val jsonText = if (serializer != null) json.encodeToString(serializer, data) else json.encodeToString(data)
        file.writeText(jsonText)
        val elapsed = System.currentTimeMillis() - start
        StardewValley.instance.loggerMessage("Saved '$fileName' in ${elapsed}ms")
    }

    private inline fun <reified T> load(fileName: String, serializer: KSerializer<T>? = null): T? {
        val file = File(dataFolder, fileName)
        if (!file.exists()) return null

        val start = System.currentTimeMillis()
        val jsonText = file.readText()
        val result = if (serializer != null) json.decodeFromString(serializer, jsonText)
        else json.decodeFromString(jsonText)
        val elapsed = System.currentTimeMillis() - start

        StardewValley.instance.loggerMessage("Loaded '$fileName' in ${elapsed}ms")
        return result
    }

    private fun defaultGameMeta() = GameData(6, 0, Season.Spring, 1)
}