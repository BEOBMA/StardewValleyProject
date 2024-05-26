package org.beobma.stardewvalleyproject.time

import org.beobma.stardewvalleyproject.StardewValley
import org.beobma.stardewvalleyproject.info.InfoManager
import org.beobma.stardewvalleyproject.info.InfoManager.Companion.players
import org.beobma.stardewvalleyproject.system.SystemManager
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

class TimeManager {
    companion object {
        var gameTime = 0L
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
        gameTime = 0
    }

    private fun startGameTime() {
        bukkitTask = object : BukkitRunnable() {
            override fun run() {
                gameTime += 20

                if (gameTime >= 24000) {
                    gameTime -= 24000
                }

                for (world in Bukkit.getWorlds()) {
                    world.time = gameTime
                }

                if (gameTime in 18000..23999) {
                    handlePlayerFaint()
                }
            }
        }.runTaskTimer(StardewValley.instance, 0, 37)
    }

    private fun handlePlayerFaint() {
        StardewValley().loggerMessage("StardewValley Player Time Over")
        timeStop()

        val systemManager = SystemManager()

        systemManager.run {
            players.forEach {
                it.faint()
            }
        }

        timePlay()
    }


    fun getHour(): Int {
        val totalMinutes = (gameTime * 1440) / 24000
        val hours = (totalMinutes / 60).toInt()

        return hours
    }

    fun getMinutes(): Int {
        val totalMinutes = (gameTime * 1440) / 24000
        val minutes = (totalMinutes % 60).toInt()

        return minutes
    }
}