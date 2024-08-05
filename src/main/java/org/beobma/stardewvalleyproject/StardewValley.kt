package org.beobma.stardewvalleyproject

import org.beobma.stardewvalleyproject.config.PlantConfig
import org.beobma.stardewvalleyproject.listener.*
import org.beobma.stardewvalleyproject.manager.DataManager
import org.beobma.stardewvalleyproject.manager.DefaultDataHandler
import org.beobma.stardewvalleyproject.manager.DefaultDataHandler.Companion.gameData
import org.beobma.stardewvalleyproject.manager.DefaultTimeHandler
import org.beobma.stardewvalleyproject.manager.TimeManager
import org.bukkit.plugin.java.JavaPlugin

class StardewValley : JavaPlugin() {

    companion object {
        lateinit var instance: StardewValley
    }

    override fun onEnable() {
        instance = this

        val dataManager = DataManager(DefaultDataHandler())
        registerEvents()
        pluginConfig()
        dataManager.loadData()

        loggerMessage("StardewValley Plugin Enable")
    }

    override fun onDisable() {
        val timeManager = TimeManager(DefaultTimeHandler())
        val dataManager = DataManager(DefaultDataHandler())

        gameData.players.clear()
        timeManager.timePause()
        dataManager.saveData()

        loggerMessage("StardewValley Plugin Disable")
    }

    private fun pluginConfig() {
        PlantConfig()
    }

    private fun registerEvents() {
        server.pluginManager.registerEvents(OnInventoryOpen(), this)
        server.pluginManager.registerEvents(OnInventoryClose(), this)
        server.pluginManager.registerEvents(OnPlayerInteract(), this)
        server.pluginManager.registerEvents(OnPlayerJoin(), this)
        server.pluginManager.registerEvents(OnPlayerQuit(), this)
    }


    fun loggerMessage(msg: String) {
        logger.info(msg)
    }
}
