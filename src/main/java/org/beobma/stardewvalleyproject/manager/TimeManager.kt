package org.beobma.stardewvalleyproject.manager

import org.beobma.stardewvalleyproject.StardewValley
import org.beobma.stardewvalleyproject.manager.DataManager.gameData
import org.beobma.stardewvalleyproject.manager.DataManager.interactionFarmlands
import org.beobma.stardewvalleyproject.manager.DataManager.plantList
import org.beobma.stardewvalleyproject.manager.FarmingManager.growth
import org.beobma.stardewvalleyproject.manager.MineManager.nextDay
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.plant.list.DeadGrassPlant
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.type.Farmland
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

interface TimeHandler {
    fun timePlay()
    fun timePause()
    fun getHour(): Int
    fun getMinutes(): Int
    fun getSeason(): Season
}

object TimeManager : TimeHandler {
    private var timeTask: BukkitTask? = null
    private val seasonTimeMapping = mapOf(
        Season.Spring to mapOf(
            6 to 22917, 7 to 23450, 8 to 450, 9 to 1000, 10 to 2000, 11 to 4000,
            12 to 6000, 13 to 7700, 14 to 8300, 15 to 9000, 16 to 10000,
            17 to 11500, 18 to 12000, 19 to 13050, 20 to 13800, 21 to 14500,
            22 to 15500, 23 to 17000, 24 to 18000, 1 to 18000, 2 to 18000
        ),
        Season.Summer to mapOf(
            6 to 22917, 7 to 23450, 8 to 450, 9 to 1000, 10 to 2000, 11 to 4000,
            12 to 6000, 13 to 7700, 14 to 8300, 15 to 9000, 16 to 10000,
            17 to 11500, 18 to 12000, 19 to 13050, 20 to 13800, 21 to 14500,
            22 to 15500, 23 to 17000, 24 to 18000, 1 to 18000, 2 to 18000
        ),
        Season.Autumn to mapOf(
            6 to 23917, 7 to 24450, 8 to 1450, 9 to 2000, 10 to 3000, 11 to 5000,
            12 to 7000, 13 to 8700, 14 to 9300, 15 to 10000, 16 to 11000,
            17 to 12500, 18 to 13000, 19 to 14050, 20 to 14800, 21 to 16500,
            22 to 16500, 23 to 17000, 24 to 17500, 1 to 18000, 2 to 18000
        ),
        Season.Winter to mapOf(
            6 to 22350, 7 to 22917, 8 to 23450, 9 to 450, 10 to 1000, 11 to 2000,
            12 to 4000, 13 to 6000, 14 to 7700, 15 to 8300, 16 to 9000,
            17 to 10000, 18 to 11500, 19 to 12000, 20 to 13050, 21 to 13800,
            22 to 14500, 23 to 15500, 24 to 17000, 1 to 18000, 2 to 18000
        )
    )

    override fun timePlay() {
        if (timeTask != null) return
        timeTask = object : BukkitRunnable() {
            override fun run() {
                advanceTime()

                if (gameData.hour in 3..5) {
                    endOfDay()
                    return
                }

                Bukkit.getWorld("world")?.time = getMinecraftTime(gameData.season, gameData.hour).toLong()
            }
        }.runTaskTimer(StardewValley.instance, 0, 125L)
    }

    override fun timePause() {
        timeTask?.cancel()
        timeTask = null
    }

    override fun getHour(): Int = gameData.hour
    override fun getMinutes(): Int = gameData.minute
    override fun getSeason(): Season = gameData.season


    private fun advanceTime() {
        gameData.minute += 10
        if (gameData.minute >= 60) {
            gameData.hour += 1
            gameData.minute = 0
        }
    }

    private fun getMinecraftTime(season: Season, hour: Int): Int {
        return seasonTimeMapping[season]?.get(hour) ?: 18000
    }

    fun endOfDay() {
        gameData.day++
        resetTime()

        plantList.forEach { it.growth() }

        val blocksToDry = hashSetOf<Location>()

        interactionFarmlands.forEach { block ->
            val farmland = block.block.blockData as? Farmland ?: return@forEach
            val plant = plantList.find { it.farmlandLocation == block.block.location }

            if (plant is DeadGrassPlant) return@forEach
            if (plant !is Plant && farmland.moisture != farmland.maximumMoisture) {
                block.block.type = Material.DIRT
                blocksToDry.add(block)
                return@forEach
            }

            farmland.moisture = 0
            block.block.blockData = farmland
        }

        interactionFarmlands.removeAll(blocksToDry)

        nextDay()
        // 플레이어 기절 시
        // gameData.hour += 3
        timePlay()
    }

    private fun resetTime() {
        timePause()
        gameData.hour = 6
        gameData.minute = 0
    }
}