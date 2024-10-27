package org.beobma.stardewvalleyproject.plant

import net.kyori.adventure.text.Component
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class CornerPlant : Plant("옥수수", 5, 3, listOf(Season.Summer)) {
    override fun getSeedItem(): ItemStack {
        return ItemStack(Material.WHEAT_SEEDS, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("옥수수 씨앗"))
                lore(
                    listOf(Component.text("옥수수 씨앗 설명"))
                )
            }
        }
    }

    override fun getHarvestItem(): ItemStack {
        return ItemStack(Material.WHEAT, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("옥수수"))
                lore(
                    listOf(Component.text("옥수수 설명"))
                )
            }
        }
    }
}