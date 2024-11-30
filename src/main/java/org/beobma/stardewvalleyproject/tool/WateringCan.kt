package org.beobma.stardewvalleyproject.tool

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class WateringCan {
    private val wateringCan = ItemStack(Material.PAPER, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(Component.text("물뿌리개"))
            lore(
                listOf(Component.text("심어진 작물에 우클릭 하면 물을 줄 수 있습니다."))
            )
            isUnbreakable = true
        }
    }
    private val pumpWateringCan = ItemStack(Material.PAPER, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(Component.text("펌프형 물뿌리개"))
            lore(
                listOf(Component.text("심어진 작물에 우클릭 하면 물을 줄 수 있습니다."))
            )
            isUnbreakable = true
        }
    }

    val wateringCans = hashSetOf<ItemStack>(wateringCan, pumpWateringCan)
}