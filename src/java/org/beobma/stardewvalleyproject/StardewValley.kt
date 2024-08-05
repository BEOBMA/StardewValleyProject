package org.beobma.stardewvalleyproject

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.serializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import org.beobma.stardewvalleyproject.listener.*
import org.beobma.stardewvalleyproject.manager.DataManager
import org.beobma.stardewvalleyproject.manager.DefaultDataHandler
import org.beobma.stardewvalleyproject.manager.DefaultDataHandler.Companion.gameData
import org.beobma.stardewvalleyproject.manager.DefaultTimeHandler
import org.beobma.stardewvalleyproject.manager.TimeManager
import org.beobma.stardewvalleyproject.serializer.ItemStackSerializer
import org.bukkit.plugin.java.JavaPlugin

class StardewValley : JavaPlugin() {

    companion object {
        lateinit var instance: StardewValley
    }

    override fun onEnable() {
        val dataManager = DataManager(DefaultDataHandler())

        instance = this
        registerEvents()
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