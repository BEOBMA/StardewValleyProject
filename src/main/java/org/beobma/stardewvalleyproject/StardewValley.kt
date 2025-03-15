package org.beobma.stardewvalleyproject

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import org.beobma.stardewvalleyproject.listener.*
import org.beobma.stardewvalleyproject.manager.DataManager.gameData
import org.beobma.stardewvalleyproject.manager.DataManager.loadData
import org.beobma.stardewvalleyproject.manager.DataManager.saveData
import org.beobma.stardewvalleyproject.manager.TimeManager.timePause
import org.bukkit.plugin.java.JavaPlugin

class StardewValley : JavaPlugin() {

    companion object {
        lateinit var instance: StardewValley
        lateinit var protocolManager: ProtocolManager
    }

    override fun onEnable() {
        instance = this
        protocolManager = ProtocolLibrary.getProtocolManager();
        registerEvents()
        loadData()

        loggerMessage("StardewValley Plugin Enable")
    }

    override fun onDisable() {
        gameData.players.clear()
        timePause()
        saveData()

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
