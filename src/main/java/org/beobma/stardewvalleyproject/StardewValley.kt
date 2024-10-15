package org.beobma.stardewvalleyproject

import org.beobma.stardewvalleyproject.config.PlantConfig
import org.beobma.stardewvalleyproject.listener.*
import org.beobma.stardewvalleyproject.manager.DataManager.gameData
import org.beobma.stardewvalleyproject.manager.DataManager.loadData
import org.beobma.stardewvalleyproject.manager.DataManager.saveData
import org.beobma.stardewvalleyproject.manager.TimeManager.timePause
import org.bukkit.plugin.java.JavaPlugin

class StardewValley : JavaPlugin() {

    companion object {
        lateinit var instance: StardewValley
    }

    override fun onEnable() {
        instance = this
        registerEvents()
        pluginConfig()
        loadData()

        loggerMessage("StardewValley Plugin Enable")
    }

    override fun onDisable() {
        gameData.players.clear()
        timePause()
        saveData()

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
