package org.beobma.stardewvalleyproject

import org.beobma.stardewvalleyproject.data.DataManager
import org.beobma.stardewvalleyproject.data.DataManager.Companion.timeSetData
import org.beobma.stardewvalleyproject.event.EventManager
import org.beobma.stardewvalleyproject.time.TimeManager
import org.beobma.stardewvalleyproject.time.TimeManager.Companion.gameTime
import org.bukkit.plugin.java.JavaPlugin

class StardewValley : JavaPlugin() {

    companion object {
        lateinit var instance: StardewValley
    }

    override fun onEnable() {
        instance = this
        server.pluginManager.registerEvents(EventManager(), this)

        DataManager().loadData()

        logger.info("StardewValley Plugin Enable")
    }

    override fun onDisable() {
        TimeManager().timePause()
        timeSetData.time = gameTime
        DataManager().saveData()

        logger.info("StardewValley Plugin Disable")
    }


    fun loggerMessage(p1: String) {
        logger.info(p1)
    }
}
