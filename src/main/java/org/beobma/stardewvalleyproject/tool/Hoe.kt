package org.beobma.stardewvalleyproject.tool

import net.kyori.adventure.text.minimessage.MiniMessage
import org.beobma.stardewvalleyproject.manager.ToolManager.setMaxCustomDurability
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Hoe {
    val durableHoe = ItemStack(Material.WOODEN_SHOVEL, 1).apply {
        val meta = itemMeta ?: return@apply
        meta.displayName(MiniMessage.miniMessage().deserialize("단단한 괭이"))
        meta.lore(
            listOf(MiniMessage.miniMessage().deserialize("<gray>땅을 갈 수 있는 괭이. 조금 더 튼튼하다."))
        )
        meta.setCustomModelData(4)
        itemMeta = meta
    }
    val lightAndSturdyHoe = ItemStack(Material.WOODEN_SHOVEL, 1).apply {
        val meta = itemMeta ?: return@apply
        meta.displayName(MiniMessage.miniMessage().deserialize("가볍고 단단한 괭이"))
        meta.lore(
            listOf(
                MiniMessage.miniMessage().deserialize("<gray>땅을 갈 수 있는 괭이. 한번에 더 큰 면적을 갈 수 있다.")
            )
        )
        meta.setCustomModelData(5)
        itemMeta = meta
    }
    val autoHoe = ItemStack(Material.WOODEN_SHOVEL, 1).apply {
        val meta = itemMeta ?: return@apply
        meta.displayName(MiniMessage.miniMessage().deserialize("자동화 괭이"))
        meta.lore(
            listOf(
                MiniMessage.miniMessage().deserialize("<gray>땅을 갈 수 있는 괭이. 한번에 더 큰 면적을 갈 수 있다."),
                MiniMessage.miniMessage().deserialize("<gray>왼손에 들고있는 씨앗을 자동으로 심는다.")
            )
        )
        meta.setCustomModelData(6)
        itemMeta = meta
    }

    init {
        durableHoe.setMaxCustomDurability(150)
        lightAndSturdyHoe.setMaxCustomDurability(450)
        autoHoe.setMaxCustomDurability(950)
    }

    val hoes = hashSetOf(durableHoe, lightAndSturdyHoe, autoHoe)
}