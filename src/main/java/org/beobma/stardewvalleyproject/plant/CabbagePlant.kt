package org.beobma.stardewvalleyproject.plant

import net.kyori.adventure.text.Component
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class CabbagePlant : Plant("양배추", 4, 4, listOf(Season.Spring)) {
    override fun getSeedItem(): ItemStack {
        return ItemStack(Material.WHEAT_SEEDS, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("양배추 씨앗"))
                lore(
                    listOf(Component.text("성장하려면 5일이 소요됩니다."))
                )
            }
        }
    }

    override fun getHarvestItem(): ItemStack {
        return ItemStack(Material.WHEAT, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("양배추"))
                lore(
                    listOf(Component.text("서양의 배추입니다. 영양소가 풍부합니다."))
                )
            }
        }
    }
}