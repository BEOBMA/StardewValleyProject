package org.beobma.stardewvalleyproject.plant

import net.kyori.adventure.text.Component
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class TomatoPlant : Plant("토마토", 3, 5, listOf(Season.Summer)) {
    override fun getSeedItem(): ItemStack {
        return ItemStack(Material.WHEAT_SEEDS, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("토마토 씨앗"))
                lore(
                    listOf(Component.text("토마토 씨앗 설명"))
                )
            }
        }
    }

    override fun getHarvestItem(): ItemStack {
        return ItemStack(Material.POTATO, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("토마토"))
                lore(
                    listOf(Component.text("토마토 설명"))
                )
            }
        }
    }
}