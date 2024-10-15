package org.beobma.stardewvalleyproject.plant

import net.kyori.adventure.text.Component
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class DeadGrassPlant : Plant("죽은 풀", 999999, 1, listOf(Season.Spring, Season.Summer, Season.Autumn, Season.Winter)) {
    override fun getSeedItem(): ItemStack {
        return ItemStack(Material.GRASS, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("죽은 풀 씨앗"))
                lore(
                    listOf(Component.text("쓸모는 없겠지만. 다음날이 되면 죽은 풀이 자라난다."))
                )
            }
        }
    }

    override fun getHarvestItem(): ItemStack {
        return ItemStack(Material.GRASS, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("죽은 풀"))
                lore(
                    listOf(Component.text("달리 쓸 방도가 없는 풀."))
                )
            }
        }
    }
}