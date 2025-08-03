package org.beobma.stardewvalleyproject.listener

import org.beobma.stardewvalleyproject.manager.TimeManager.timePlay
import org.beobma.stardewvalleyproject.manager.UtilManager.isSingle
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

class OnInventoryClose : Listener {
    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        if (!isSingle()) return
        timePlay()
    }
}