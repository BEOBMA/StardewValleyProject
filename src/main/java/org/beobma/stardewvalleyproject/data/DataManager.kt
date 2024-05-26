package org.beobma.stardewvalleyproject.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.beobma.stardewvalleyproject.StardewValley
import org.beobma.stardewvalleyproject.info.InfoManager.Companion.plant
import org.beobma.stardewvalleyproject.time.TimeManager.Companion.gameTime
import java.io.File

@Serializable
data class TimeSetData(var time: Long)

class DataManager {
    private val folder: File = File(StardewValley.instance.dataFolder, "/data")
    private val json = Json { prettyPrint = true }

    companion object {
        var timeSetData = TimeSetData(0L)
    }


    fun saveData() {
        StardewValley().loggerMessage("StardewValley Plugin Data Save")
        val timeData = json.encodeToString(timeSetData)
        val plantData = json.encodeToString(plant)

        File(folder, "TimeData.json").writeText(timeData)
        File(folder, "PlantData.json").writeText(plantData)
    }

    fun loadData() {
        StardewValley().loggerMessage("StardewValley Plugin Data Load")
        if (!folder.exists()) {
            folder.mkdirs()
            return
        }
        val timeFile = File(folder, "TimeData.json")
        val plantFile = File(folder, "PlantData.json")
        val timeData = timeFile.readText()
        val plantData = plantFile.readText()
        timeSetData = json.decodeFromString(timeData)
        plant = json.decodeFromString(plantData)
        gameTime = timeSetData.time
    }
}