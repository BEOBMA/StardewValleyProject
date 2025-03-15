package org.beobma.stardewvalleyproject.manager

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.beobma.stardewvalleyproject.StardewValley
import org.beobma.stardewvalleyproject.data.GameData
import org.beobma.stardewvalleyproject.mine.Mine
import org.beobma.stardewvalleyproject.mine.MineTemplate
import org.beobma.stardewvalleyproject.mine.MineType
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.plant.list.DeadGrassPlant
import org.beobma.stardewvalleyproject.resource.Resource
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.io.File
import java.lang.reflect.Type

interface DataHandler {
    fun saveData()
    fun loadData()
}

object DataManager : DataHandler {
    private val folder = File(StardewValley.instance.dataFolder, "data")
    private val dataFile get() = File(folder, "data.json")
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Plant::class.java, PlantAdapter())
        .registerTypeAdapter(Block::class.java, BlockTypeAdapter())
        .registerTypeAdapter(Mine::class.java, MineTypeAdapter())
        .enableComplexMapKeySerialization()
        .setPrettyPrinting()
        .create()

    var gameData = GameData(
        hour = 6,
        minute = 0,
        season = Season.Spring,
        day = 1,
        players = hashSetOf(),
        plantList = mutableListOf(),
        blockToPlantMap = mutableMapOf(),
        interactionFarmlands = hashSetOf(),
        mines = mutableListOf()
    )

    override fun saveData() {
        logMessage("StardewValley Plugin Data Save")
        try {
            if (!folder.exists()) folder.mkdirs()
            dataFile.writeText(gson.toJson(gameData))
        } catch (e: Exception) {
            logMessage("Failed to save data: ${e.message}")
        }
    }

    override fun loadData() {
        logMessage("StardewValley Plugin Data Load")
        try {
            if (!folder.exists()) folder.mkdirs()
            if (dataFile.exists()) {
                val json = dataFile.readText()
                val loadedData = gson.fromJson(json, GameData::class.java)
                gameData = loadedData
            } else {
                logMessage("Data file not found, initializing default values.")
                createDefaultGameData()
            }
        } catch (e: Exception) {
            logMessage("Failed to load data: ${e.message}")
            createDefaultGameData()
        }
    }

    private fun createDefaultGameData() {
        gameData = GameData(
            hour = 6,
            minute = 0,
            season = Season.Spring,
            day = 1,
            players = hashSetOf(),
            plantList = mutableListOf(),
            blockToPlantMap = mutableMapOf(),
            interactionFarmlands = hashSetOf(),
            mines = mutableListOf()
        )
    }

    private fun logMessage(message: String) {
        StardewValley.instance.loggerMessage(message)
    }
}

class PlantAdapter : JsonSerializer<Plant>, JsonDeserializer<Plant> {
    override fun serialize(src: Plant, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val properties = JsonObject().apply {
            addProperty("name", src.name)
            addProperty("harvestCycle", src.harvestCycle)
            addProperty("yield", src.yield)
            add("plantSeasons", context.serialize(src.plantSeasons))
            addProperty("isPlant", src.isPlant)
            if (src.block != null) {
                add("block", context.serialize(src.block, Block::class.java))
            } else {
                add("block", JsonNull.INSTANCE)
            }
            addProperty("isHarvestComplete", src.isHarvestComplete)
            addProperty("isWeeds", src.isWeeds)
            addProperty("weedsCount", src.weedsCount)
            add("capsuleType", context.serialize(src.capsuleType))
        }
        return JsonObject().apply {
            addProperty("type", src::class.java.name)
            add("properties", properties)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Plant {
        val wrapperObj = json.asJsonObject
        val className = wrapperObj.get("type").asString
        val properties = wrapperObj.getAsJsonObject("properties")
        val clazz = Class.forName(className) as? Class<Plant> ?: return DeadGrassPlant()
        return context.deserialize(properties, clazz)
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

class MineTypeAdapter : JsonSerializer<Mine>, JsonDeserializer<Mine> {
    override fun serialize(src: Mine, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonObject().apply {
            addProperty("floor", src.floor)
            addProperty("mineTemplate", src.mineTemplate.name)
            addProperty("mineType", src.mineType.name)
            add("resources", context.serialize(src.resources))
            add("players", context.serialize(src.players))
            add("enemys", context.serialize(src.enemys))
        }
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Mine {
        val jsonObject = json.asJsonObject
        val floor = jsonObject.get("floor").asInt
        val mineTemplateName = jsonObject.get("mineTemplate").asString
        val mineTypeName = jsonObject.get("mineType").asString
        val mineTemplate = MineTemplate.entries.find { it.name == mineTemplateName }
            ?: throw JsonParseException("MineTemplate not found: $mineTemplateName")
        val mineType = MineType.entries.find { it.name == mineTypeName }
            ?: throw JsonParseException("MineType not found: $mineTypeName")

        val resourcesType = object : TypeToken<MutableList<Resource>>() {}.type
        val resources: MutableList<Resource> = context.deserialize(jsonObject.get("resources"), resourcesType)
        val playersType = object : TypeToken<MutableList<Player>>() {}.type
        val players: MutableList<Player> = context.deserialize(jsonObject.get("players"), playersType)
        val enemyType = object : TypeToken<MutableList<Entity>>() {}.type
        val enemys: MutableList<Entity> = context.deserialize(jsonObject.get("enemys"), enemyType)
        return Mine(floor, mineTemplate, mineType, resources, enemys, players)
    }
}
