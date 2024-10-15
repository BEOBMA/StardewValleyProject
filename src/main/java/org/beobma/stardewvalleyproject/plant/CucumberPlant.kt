package org.beobma.stardewvalleyproject.plant

import net.kyori.adventure.text.Component
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class CucumberPlant : Plant("오이", 4, 5, listOf(Season.Spring)) {
    override fun getSeedItem(): ItemStack {
        return ItemStack(Material.WHEAT_SEEDS, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("오이 씨앗"))
                lore(
                    listOf(Component.text("성장하려면 4일이 소요됩니다."))
                )
            }
        }
    }

    override fun getHarvestItem(): ItemStack {
        return ItemStack(Material.WHEAT, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("오이"))
                lore(
                    listOf(Component.text("한해살이 덩굴풀의 열매입니다."))
                )
            }
        }
    }
}