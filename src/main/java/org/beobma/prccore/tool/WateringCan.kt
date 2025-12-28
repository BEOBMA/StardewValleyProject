package org.beobma.prccore.tool

import net.kyori.adventure.text.minimessage.MiniMessage
import org.beobma.prccore.manager.ToolManager.setMaxCustomDurability
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class WateringCan {
    val wateringCan = ItemStack(Material.WOODEN_SHOVEL, 1).apply {
        val meta = itemMeta ?: return@apply
        meta.displayName(MiniMessage.miniMessage().deserialize("물뿌리개"))
        meta.lore(
            listOf(MiniMessage.miniMessage().deserialize("<gray>경작지나 식물에 물을 줄 수 있다."))
        )
        meta.setCustomModelData(7)
        itemMeta = meta
    }

    val pumpWateringCan = ItemStack(Material.WOODEN_SHOVEL, 1).apply {
        val meta = itemMeta ?: return@apply
        meta.displayName(MiniMessage.miniMessage().deserialize("펌프형 물뿌리개"))
        meta.lore(
            listOf(MiniMessage.miniMessage().deserialize("<gray>더 넓은 범위로 물을 줄 수 있다."))
        )
        meta.setCustomModelData(8)
        itemMeta = meta
    }

    init {
        wateringCan.setMaxCustomDurability(450)
        pumpWateringCan.setMaxCustomDurability(950)
    }

    val wateringCans = hashSetOf(wateringCan, pumpWateringCan)
}