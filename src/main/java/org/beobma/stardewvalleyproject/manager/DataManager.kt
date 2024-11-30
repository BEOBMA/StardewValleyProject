package org.beobma.stardewvalleyproject.manager

import com.google.gson.*
import org.beobma.stardewvalleyproject.StardewValley
import org.beobma.stardewvalleyproject.data.GameData
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.Bukkit
import org.bukkit.block.Block
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.lang.reflect.Type

interface DataHandler {
    fun saveData()
    fun loadData()
}


object DataManager : DataHandler {
    private val folder: File = File(StardewValley.instance.dataFolder, "/data")
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Plant::class.java, PlantAdapter())
        .registerTypeAdapter(Block::class.java, BlockTypeAdapter())
        .enableComplexMapKeySerialization()
        .setPrettyPrinting()
        .create()
    var gameData = GameData(6, 0, Season.Spring, 1, hashSetOf(), hashSetOf(), hashMapOf(), hashSetOf())

    override fun saveData() {
        logMessage("StardewValley Plugin Data Save")
        try {
            if (!folder.exists()) {
                folder.mkdirs()
            }
            val dataFile = File(folder, "data.json")
            FileWriter(dataFile).use { writer ->
                writer.write(gson.toJson(gameData))
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
                    val loadedData = gson.fromJson(reader, GameData::class.java)
                    if (loadedData is GameData) {
                        gameData = loadedData
                    } else {
                        logMessage("Loaded data is not valid, initializing with default values.")
                        gameData = GameData(6, 0, Season.Spring, 1, hashSetOf(), hashSetOf(), hashMapOf(), hashSetOf())
                    }
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

class PlantAdapter : JsonSerializer<Plant>, JsonDeserializer<Plant> {
    override fun serialize(src: Plant, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty("type", src::class.java.name)
        jsonObject.add("properties", context.serialize(src))
        return jsonObject
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Plant {
        val jsonObject = json.asJsonObject
        val type = Class.forName(jsonObject.get("type").asString)
        return context.deserialize(jsonObject.get("properties"), type)
    }
}

class BlockTypeAdapter : JsonSerializer<Block>, JsonDeserializer<Block> {
    override fun serialize(src: Block, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive("${src.world.name},${src.x},${src.y},${src.z}")
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Block {
        val parts = json.asString.split(",")
        val world = Bukkit.getWorld(parts[0])
            ?: throw JsonParseException("World not found: ${parts[0]}")
        val x = parts[1].toIntOrNull() ?: throw JsonParseException("Invalid X coordinate: ${parts[1]}")
        val y = parts[2].toIntOrNull() ?: throw JsonParseException("Invalid Y coordinate: ${parts[2]}")
        val z = parts[3].toIntOrNull() ?: throw JsonParseException("Invalid Z coordinate: ${parts[3]}")
        return world.getBlockAt(x, y, z)
    }
}