package org.beobma.stardewvalleyproject.manager

import org.beobma.stardewvalleyproject.StardewValley
import org.beobma.stardewvalleyproject.manager.AbnormalStatusManager.faint
import org.beobma.stardewvalleyproject.manager.DefaultDataHandler.Companion.gameData
import org.beobma.stardewvalleyproject.manager.PlantManager.growth
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

interface TimeHandler {
    fun timePlay()
    fun timePause()
    fun getHour(): Int
    fun getMinutes(): Int
    fun getSeason(): Season
}

class DefaultTimeHandler : TimeHandler {
    companion object {
        private var timeBukkitTask: BukkitTask? = null
    }

    override fun timePlay() {
        startGameTime()
    }

    override fun timePause() {
        timeBukkitTask?.cancel()
        timeBukkitTask = null
    }

    override fun getHour(): Int {
        return gameData.hour
    }

    override fun getMinutes(): Int {
        return gameData.minute
    }

    override fun getSeason(): Season {
        return gameData.season
    }


    private fun timeStop() {
        timeBukkitTask?.cancel()
        timeBukkitTask = null
        gameData.hour = 6
        gameData.minute = 0
    }

    private fun startGameTime() {
        if (timeBukkitTask is BukkitTask) return

        timeBukkitTask = object : BukkitRunnable() {
            override fun run() {
                gameData.minute += 10

                if (gameData.minute >= 60) {
                    gameData.hour += 1
                    gameData.minute = 0
                }

                // 데드라인 02시
                if (gameData.hour in 2..5) {
                    dayEnd()
                }

                val minecraftTime = when (gameData.season) {
                    Season.Spring -> when (gameData.hour) {
                        6 -> 22917
                        7 -> 23450
                        8 -> 450
                        9 -> 1000
                        10 -> 2000
                        11 -> 4000
                        12 -> 6000
                        13 -> 7700
                        14 -> 8300
                        15 -> 9000
                        16 -> 10000
                        17 -> 11500
                        18 -> 12000
                        19 -> 13050
                        20 -> 13800
                        21 -> 14500
                        22 -> 15500
                        23 -> 17000
                        24 -> 18000
                        1 -> 18000
                        2 -> 18000
                        else -> 18000
                    }
                    Season.Summer -> when (gameData.hour) {
                        6 -> 22917
                        7 -> 23450
                        8 -> 450
                        9 -> 1000
                        10 -> 2000
                        11 -> 4000
                        12 -> 6000
                        13 -> 7700
                        14 -> 8300
                        15 -> 9000
                        16 -> 10000
                        17 -> 11500
                        18 -> 12000
                        19 -> 13050
                        20 -> 13800
                        21 -> 14500
                        22 -> 15500
                        23 -> 17000
                        24 -> 18000
                        1 -> 18000
                        2 -> 18000
                        else -> 18000
                    }
                    Season.Autumn -> when (gameData.hour) {
                        6 -> 23917
                        7 -> 24450
                        8 -> 1450
                        9 -> 2000
                        10 -> 3000
                        11 -> 5000
                        12 -> 7000
                        13 -> 8700
                        14 -> 9300
                        15 -> 10000
                        16 -> 11000
                        17 -> 12500
                        18 -> 13000
                        19 -> 14050
                        20 -> 14800
                        21 -> 16500
                        22 -> 16500
                        23 -> 17000
                        24 -> 17500
                        1 -> 18000
                        2 -> 18000
                        else -> 18000
                    }
                    Season.Winter -> when (gameData.hour) {
                        6 -> 22350
                        7 -> 22917
                        8 -> 23450
                        9 -> 450
                        10 -> 1000
                        11 -> 2000
                        12 -> 4000
                        13 -> 6000
                        14 -> 7700
                        15 -> 8300
                        16 -> 9000
                        17 -> 10000
                        18 -> 11500
                        19 -> 12000
                        20 -> 13050
                        21 -> 13800
                        22 -> 14500
                        23 -> 15500
                        24 -> 17000
                        1 -> 18000
                        2 -> 18000
                        else -> 18000
                    }
                    else -> when (gameData.hour) {
                        6 -> 22917
                        7 -> 23450
                        8 -> 450
                        9 -> 1000
                        10 -> 2000
                        11 -> 4000
                        12 -> 6000
                        13 -> 7700
                        14 -> 8300
                        15 -> 9000
                        16 -> 10000
                        17 -> 11500
                        18 -> 12000
                        19 -> 13050
                        20 -> 13800
                        21 -> 14500
                        22 -> 15500
                        23 -> 17000
                        24 -> 18000
                        1 -> 18000
                        2 -> 18000
                        else -> 18000
                    }
                }

                val world = Bukkit.getWorld("world")
                world?.time = minecraftTime.toLong()
            }
        }.runTaskTimer(StardewValley.instance, 0, 125)
    }

    private fun handlePlayerFaint() {
        logMessage("StardewValley Player Time Over")
        timeStop()

        gameData.players.forEach {
            it.faint()
        }

        timePlay()
    }

    private fun dayEnd() {
        logMessage("StardewValley Day End")

        gameData.day++
        gameData.hour = 6
        gameData.minute = 0
        gameData.plantList.forEach {
            it.growth()
        }

        if (gameData.day > 28) {
            gameData.season = when (gameData.season) {
                Season.Spring -> Season.Summer
                Season.Summer -> Season.Autumn
                Season.Autumn -> Season.Winter
                Season.Winter -> Season.Spring
                else -> Season.Spring
            }
            gameData.day = 1
        }
        handlePlayerFaint()
    }

    private fun logMessage(message: String) {
        StardewValley.instance.loggerMessage(message)
    }
}

object TimeManager {
    private val handler: TimeHandler = DefaultTimeHandler()

    fun timePlay() {
        handler.timePlay()
    }

    fun timePause() {
        handler.timePause()
    }

    fun getHour(): Int {
        return handler.getHour()
    }

    fun getMinutes(): Int {
        return handler.getMinutes()
    }

    fun getSeason(): Season {
        return handler.getSeason()
    }
}