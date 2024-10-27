package org.beobma.stardewvalleyproject.plant

import net.kyori.adventure.text.Component
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class PumpkinPlant : Plant("호박", 8, 2, listOf(Season.Autumn)) {
    override fun getSeedItem(): ItemStack {
        return ItemStack(Material.WHEAT_SEEDS, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("호박 씨앗"))
                lore(
                    listOf(Component.text("호박 씨앗 설명"))
                )
            }
        }
    }

    override fun getHarvestItem(): ItemStack {
        return ItemStack(Material.WHEAT, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("호박"))
                lore(
                    listOf(Component.text("호박 설명"))
                )
            }
        }
    }
}