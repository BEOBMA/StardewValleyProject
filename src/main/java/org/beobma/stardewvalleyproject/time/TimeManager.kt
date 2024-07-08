package org.beobma.stardewvalleyproject.time

import org.beobma.stardewvalleyproject.StardewValley
import org.beobma.stardewvalleyproject.data.DataManager.Companion.gameData
import org.beobma.stardewvalleyproject.data.Season
import org.beobma.stardewvalleyproject.system.SystemManager
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

class TimeManager {
    companion object {
        private var bukkitTask: BukkitTask? = null
    }


    fun timePlay() {
        StardewValley().loggerMessage("StardewValley Time Play")
        startGameTime()
    }

    fun timePause() {
        StardewValley().loggerMessage("StardewValley Time Pause")
        bukkitTask?.cancel()
        bukkitTask = null
    }

    private fun timeStop() {
        StardewValley().loggerMessage("StardewValley Time Stop")
        bukkitTask?.cancel()
        bukkitTask = null
        gameData.time = 0
    }

    private fun startGameTime() {
        bukkitTask = object : BukkitRunnable() {
            override fun run() {
                gameData.time += 20

                if (gameData.time >= 24000) {
                    gameData.time -= 24000
                }

                for (world in Bukkit.getWorlds()) {
                    world.time = gameData.time
                }

                if (gameData.time in 18000..23999) {
                    dayEnd()

                    //하루가 끝남
                }
            }
        }.runTaskTimer(StardewValley.instance, 0, 37)
    }

    private fun handlePlayerFaint() {
        StardewValley().loggerMessage("StardewValley Player Time Over")
        timeStop()

        val systemManager = SystemManager()

        systemManager.run {
            gameData.players.forEach {
                it.faint()
            }
        }

        timePlay()
    }

    fun dayEnd() {
        StardewValley().loggerMessage("StardewValley Day End")
        gameData.day++

        if (gameData.day > 28) {
            when (gameData.season) {
                Season.Spring -> gameData.season = Season.Summer
                Season.Summer -> gameData.season = Season.Autumn
                Season.Autumn -> gameData.season = Season.Winter
                Season.Winter -> gameData.season = Season.Spring
            }
            gameData.day = 1
        }
        handlePlayerFaint()


    }

    fun getHour(): Int {
        val totalMinutes = (gameData.time * 1440) / 24000
        val hours = (totalMinutes / 60).toInt()

        return hours
    }

    fun getMinutes(): Int {
        val totalMinutes = (gameData.time * 1440) / 24000
        val minutes = (totalMinutes % 60).toInt()

        return minutes
    }
}