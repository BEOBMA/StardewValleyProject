package org.beobma.stardewvalleyproject.plant

import net.kyori.adventure.text.Component
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class CoffeeBeansPlant : Plant("커피콩", 10, 10, listOf(Season.Spring, Season.Summer)) {
    override fun getSeedItem(): ItemStack {
        return ItemStack(Material.WHEAT_SEEDS, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("커피콩"))
                lore(
                    listOf(Component.text("성장하려면 10일이 소요됩니다."))
                )
            }
        }
    }

    override fun getHarvestItem(): ItemStack {
        return ItemStack(Material.WHEAT, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("커피콩"))
                lore(
                    listOf(Component.text("커피의 주원료입니다."))
                )
            }
        }
    }
}