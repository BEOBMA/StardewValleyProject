package org.beobma.stardewvalleyproject.manager

import org.beobma.stardewvalleyproject.StardewValley
import org.beobma.stardewvalleyproject.manager.DefaultDataHandler.Companion.gameData
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

                val minecraftTime = (gameData.hour * 1000) + ((gameData.minute / 60.0) * 1000).toInt()
                val world = Bukkit.getWorld("world")
                world?.time = minecraftTime.toLong()
            }
        }.runTaskTimer(StardewValley.instance, 0, 125)
    }

    private fun handlePlayerFaint() {
        logMessage("StardewValley Player Time Over")
        timeStop()

        val abnormalStatusManager = AbnormalStatusManager(DefaultAbnormalStatusHandler())
        abnormalStatusManager.run {
            gameData.players.forEach {
                it.faint()
            }
        }

        timePlay()
    }

    private fun dayEnd() {
        val plantManager = PlantManager(DefaultPlantHanler())

        logMessage("StardewValley Day End")
        gameData.day++
        plantManager.run {
            gameData.plantList.forEach {
                it.growth()
            }
        }

        if (gameData.day > 28) {
            gameData.season = when (gameData.season) {
                Season.Spring -> Season.Summer
                Season.Summer -> Season.Autumn
                Season.Autumn -> Season.Winter
                Season.Winter -> Season.Spring
            }
            gameData.day = 1
        }
        handlePlayerFaint()
    }

    private fun logMessage(message: String) {
        StardewValley.instance.loggerMessage(message)
    }
}

class TimeManager(private val handler: TimeHandler) {
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