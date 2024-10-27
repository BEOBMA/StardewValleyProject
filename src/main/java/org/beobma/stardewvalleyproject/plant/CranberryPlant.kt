package org.beobma.stardewvalleyproject.plant

import net.kyori.adventure.text.Component
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class CranberryPlant : Plant("크랜베리", 6, 5, listOf(Season.Autumn)) {
    override fun getSeedItem(): ItemStack {
        return ItemStack(Material.WHEAT_SEEDS, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("크랜베리 씨앗"))
                lore(
                    listOf(Component.text("크랜베리 씨앗 설명"))
                )
            }
        }
    }

    override fun getHarvestItem(): ItemStack {
        return ItemStack(Material.WHEAT, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("크랜베리"))
                lore(
                    listOf(Component.text("크랜베리 설명"))
                )
            }
        }
    }
}