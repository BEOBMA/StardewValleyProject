package org.beobma.stardewvalleyproject.plant.list

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class DeadGrassPlant : Plant("죽은 풀", 999999, 1, listOf(Season.Spring, Season.Summer, Season.Autumn, Season.Winter)) {
    override fun getSeedItem(): ItemStack {
        return ItemStack(Material.AIR, 1)
    }

    override fun getHarvestItem(): ItemStack {
        return ItemStack(Material.AIR, 1)
    }
}