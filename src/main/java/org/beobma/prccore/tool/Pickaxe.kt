package org.beobma.prccore.tool

import net.kyori.adventure.text.minimessage.MiniMessage
import org.beobma.prccore.manager.ToolManager.setMaxCustomDurability
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Pickaxe {
    val pickaxe = ItemStack(Material.WOODEN_SHOVEL, 1).apply {
        val meta = itemMeta ?: return@apply
        meta.displayName(MiniMessage.miniMessage().deserialize("단단한 곡괭이"))
        meta.lore(listOf(MiniMessage.miniMessage().deserialize("<gray>여러 광물을 캘 수 있다.")))
        meta.setCustomModelData(1)
        itemMeta = meta
    }

    val lightDrill = ItemStack(Material.WOODEN_SHOVEL, 1).apply {
        val meta = itemMeta ?: return@apply
        meta.displayName(MiniMessage.miniMessage().deserialize("가벼운 채굴기"))
        meta.lore(listOf(MiniMessage.miniMessage().deserialize("<gray>모든 광물을 캘 수 있다.")))
        meta.setCustomModelData(2)
        itemMeta = meta
    }

    val heavyDrill = ItemStack(Material.WOODEN_SHOVEL, 1).apply {
        val meta = itemMeta ?: return@apply
        meta.displayName(MiniMessage.miniMessage().deserialize("무거운 채굴기"))
        meta.lore(listOf(MiniMessage.miniMessage().deserialize("<gray>모든 광물을 더 빠르게 캘 수 있다.")))
        meta.setCustomModelData(3)
        itemMeta = meta
    }

    init {
        pickaxe.setMaxCustomDurability(150)
        lightDrill.setMaxCustomDurability(450)
        heavyDrill.setMaxCustomDurability(950)
    }

    val pickaxes = hashSetOf(pickaxe, lightDrill, heavyDrill)
}