package org.beobma.stardewvalleyproject.listener

import org.beobma.stardewvalleyproject.manager.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType

class OnInventoryClose : Listener {
    private val utilManager = UtilManager(DefaultUtileHandler())
    private val timeManager = TimeManager(DefaultTimeHandler())

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val inventory = event.inventory

        if (!utilManager.isSingle()) return
        if (inventory.type == InventoryType.PLAYER) return
        timeManager.timePlay()
    }
}