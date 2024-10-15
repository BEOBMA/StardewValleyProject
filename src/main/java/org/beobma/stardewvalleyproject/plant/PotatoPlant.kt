package org.beobma.stardewvalleyproject.plant

import net.kyori.adventure.text.Component
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class PotatoPlant : Plant("감자", 3, 6, listOf(Season.Spring)) {
    override fun getSeedItem(): ItemStack {
        return ItemStack(Material.WHEAT_SEEDS, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("감자 씨앗"))
                lore(
                    listOf(Component.text("성장하려면 3일이 소요됩니다."))
                )
            }
        }
    }

    override fun getHarvestItem(): ItemStack {
        return ItemStack(Material.POTATO, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("감자"))
                lore(
                    listOf(Component.text("대표적인 구황작물중 하나입니다."))
                )
            }
        }
    }
}