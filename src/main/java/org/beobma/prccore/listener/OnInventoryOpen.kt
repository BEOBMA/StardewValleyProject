package org.beobma.prccore.listener

import org.beobma.prccore.manager.TimeManager.timePause
import org.beobma.prccore.manager.UtilManager.isSingle
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent

class OnInventoryOpen : Listener{
    @EventHandler
    fun onInventoryOpen(event: InventoryOpenEvent) {
        if (!isSingle()) return
        timePause()
    }
}