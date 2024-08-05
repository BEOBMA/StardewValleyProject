package org.beobma.stardewvalleyproject.listener

import org.beobma.stardewvalleyproject.manager.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

class OnInventoryClose : Listener {
    private val utilManager = UtilManager(DefaultUtileHandler())
    private val timeManager = TimeManager(DefaultTimeHandler())

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        if (!utilManager.isSingle()) return
        timeManager.timePlay()
    }
}