package org.beobma.stardewvalleyproject.listener

import org.beobma.stardewvalleyproject.manager.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

class OnInventoryClose : Listener {

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val utilManager = UtilManager(DefaultUtileHandler())
        if (!utilManager.isSingle()) return
        val timeManager = TimeManager(DefaultTimeHandler())
        timeManager.timePlay()
    }
}