package org.beobma.stardewvalleyproject.plant

import net.kyori.adventure.text.Component
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class WheatPlant : Plant("밀", 4, 1, Season.Spring) {
    override fun getSeedItem(): ItemStack {
        return ItemStack(Material.WHEAT_SEEDS, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("밀 씨앗"))
                lore(
                    listOf(Component.text("성장하려면 4일이 소요됩니다."))
                )
            }
        }
    }

    override fun getHarvestItem(): ItemStack {
        return ItemStack(Material.WHEAT, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("밀"))
                lore(
                    listOf(Component.text("널리 재배되는 곡물 중 하나입니다."))
                )
            }
        }
    }
}