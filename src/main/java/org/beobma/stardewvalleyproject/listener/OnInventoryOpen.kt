package org.beobma.stardewvalleyproject.listener

import org.beobma.stardewvalleyproject.manager.DefaultTimeHandler
import org.beobma.stardewvalleyproject.manager.DefaultUtileHandler
import org.beobma.stardewvalleyproject.manager.TimeManager
import org.beobma.stardewvalleyproject.manager.UtilManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent

class OnInventoryOpen : Listener{
    private val utilManager = UtilManager(DefaultUtileHandler())
    private val timeManager = TimeManager(DefaultTimeHandler())

    @EventHandler
    fun onInventoryOpen(event: InventoryOpenEvent) {
        if (!utilManager.isSingle()) return
        timeManager.timePause()
    }
}