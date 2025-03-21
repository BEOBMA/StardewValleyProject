package org.beobma.stardewvalleyproject.plant.list

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class CucumberPlant : Plant("오이", 4, 5, listOf(Season.Spring)) {
    override fun getSeedItem(): ItemStack {
        val itemStack = ItemStack(Material.WHEAT_SEEDS, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("오이 씨앗"))
                lore(
                    listOf(
                        MiniMessage.miniMessage().deserialize("<green><bold>봄 작물"),
                        MiniMessage.miniMessage().deserialize("<gray>재배까지 총 4일 소요")
                    )
                )
            }
        }
        return itemStack
    }

    override fun getHarvestItem(): ItemStack {
        val itemStack = ItemStack(Material.WHEAT, 1)
        itemStack.itemMeta = itemStack.itemMeta.apply {
            displayName(Component.text("오이"))
        }
        return itemStack
    }

    override fun copy(): Plant {
        return CucumberPlant()
    }
}