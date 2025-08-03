package org.beobma.stardewvalleyproject.listener

import net.kyori.adventure.text.minimessage.MiniMessage
import org.beobma.stardewvalleyproject.manager.MineManager.approach
import org.beobma.stardewvalleyproject.manager.MineManager.showMineFloorSelector
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class OnInventoryClick : Listener {
    private val miniMessage = MiniMessage.miniMessage()

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val view = event.view

        if (!view.title.startsWith("탐험할 층을 선택하세요")) return
        event.isCancelled = true

        val clickedItem = event.currentItem ?: return
        val displayName = clickedItem.itemMeta?.displayName ?: return

        when (displayName) {
            "이전 페이지" -> {
                val currentPage = player.getMetadata("mine_select_page").firstOrNull()?.asInt() ?: 0
                showMineFloorSelector(player, currentPage - 1)
            }

            "다음 페이지" -> {
                val currentPage = player.getMetadata("mine_select_page").firstOrNull()?.asInt() ?: 0
                showMineFloorSelector(player, currentPage + 1)
            }

            else -> {
                val floor = Regex("""(\d+)층""").find(displayName)?.groupValues?.get(1)?.toIntOrNull() ?: return

                player.approach(null, floor)
                player.closeInventory()
            }
        }
    }
}