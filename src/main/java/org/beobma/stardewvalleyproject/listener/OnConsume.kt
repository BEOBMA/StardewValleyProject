package org.beobma.stardewvalleyproject.listener

import org.beobma.stardewvalleyproject.manager.AdvancementManager.grantAdvancement
import org.beobma.stardewvalleyproject.manager.CustomModelDataManager.getCustomModelData
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent

class OnConsume: Listener {
    @EventHandler
    fun onConsume(event: PlayerItemConsumeEvent) {
        val player = event.player
        val item = event.item

        // 티타늄 오이 먹기
        if (item.type == Material.BLACK_DYE) {
            if (item.getCustomModelData() == 23) {
                grantAdvancement(player, "module/normal/o_e");
            }
        }
    }

}