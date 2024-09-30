package org.beobma.stardewvalleyproject.listener

import org.beobma.stardewvalleyproject.manager.*
import org.beobma.stardewvalleyproject.manager.TimeManager.timePlay
import org.beobma.stardewvalleyproject.manager.UtilManager.isSingle
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType

class OnInventoryClose : Listener {
    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val inventory = event.inventory

        if (!isSingle()) return
        if (inventory.type == InventoryType.PLAYER) return
        timePlay()
    }
}