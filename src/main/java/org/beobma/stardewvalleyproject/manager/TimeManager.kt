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
        private const val TICKS_PER_DAY = 24000
        private const val TICKS_INCREMENT = 20
        private const val DAY_END_TICKS_START = 18000
        private const val DAY_END_TICKS_END = 23999
        private const val MINUTES_IN_A_DAY = 1440
        private const val MINUTES_IN_AN_HOUR = 60

        private var bukkitTask: BukkitTask? = null
    }

    override fun timePlay() {
        logMessage("StardewValley Time Play")
        startGameTime()
    }

    override fun timePause() {
        logMessage("StardewValley Time Pause")
        bukkitTask?.cancel()
        bukkitTask = null
    }

    override fun getHour(): Int {
        val totalMinutes = (gameData.time * MINUTES_IN_A_DAY) / TICKS_PER_DAY
        return (totalMinutes / MINUTES_IN_AN_HOUR).toInt()
    }

    override fun getMinutes(): Int {
        val totalMinutes = (gameData.time * MINUTES_IN_A_DAY) / TICKS_PER_DAY
        return (totalMinutes % MINUTES_IN_AN_HOUR).toInt()
    }

    override fun getSeason(): Season {
        return gameData.season
    }

    private fun timeStop() {
        logMessage("StardewValley Time Stop")
        bukkitTask?.cancel()
        bukkitTask = null
        gameData.time = 0
    }

    private fun startGameTime() {
        bukkitTask = object : BukkitRunnable() {
            override fun run() {
                gameData.time += TICKS_INCREMENT

                if (gameData.time >= TICKS_PER_DAY) {
                    gameData.time -= TICKS_PER_DAY
                }

                for (world in Bukkit.getWorlds()) {
                    world.time = gameData.time
                }

                if (gameData.time in DAY_END_TICKS_START..DAY_END_TICKS_END) {
                    dayEnd()
                }
            }
        }.runTaskTimer(StardewValley.instance, 0, 37)
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