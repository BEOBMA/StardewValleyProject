package org.beobma.stardewvalleyproject.tool

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class WateringCan {
    private val wateringCan = ItemStack(Material.PAPER, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(MiniMessage.miniMessage().deserialize("물뿌리개"))
            lore(
                listOf(MiniMessage.miniMessage().deserialize("<gray>경작지나 식물에 물을 줄 수 있다."))
            )
        }
    }
    private val pumpWateringCan = ItemStack(Material.PAPER, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(MiniMessage.miniMessage().deserialize("펌프형 물뿌리개"))
            lore(
                listOf(MiniMessage.miniMessage().deserialize("<gray>경작지나 식물에 물을 줄 수 있다."))
            )
        }
    }

    val wateringCans = hashSetOf<ItemStack>(wateringCan, pumpWateringCan)
}