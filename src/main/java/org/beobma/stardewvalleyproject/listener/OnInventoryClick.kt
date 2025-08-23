package org.beobma.stardewvalleyproject.listener

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.beobma.stardewvalleyproject.manager.MineManager.approach
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class OnInventoryClick : Listener {
    private val miniMessage = MiniMessage.miniMessage()

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val clickedItem = event.currentItem ?: return
        val displayName = clickedItem.itemMeta?.displayName() ?: return
        val view = event.view


        if (view.title() == miniMessage.deserialize("mineShow")) {
            val displayName = LegacyComponentSerializer.legacySection().serialize(displayName)
            event.isCancelled = true
            val floor = Regex("""(\d+)층""").find(displayName)?.groupValues?.get(1)?.toIntOrNull() ?: return

            player.approach(null, floor)
            player.closeInventory()
        }
    }
}