package org.beobma.stardewvalleyproject.plant.list

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class WheatPlant : Plant("밀", 4, 8, listOf(Season.Summer, Season.Autumn)) {
    override fun getSeedItem(): ItemStack {
        val itemStack = ItemStack(Material.WHEAT_SEEDS, 1)
        itemStack.itemMeta.run {
            displayName(Component.text("밀 씨앗"))
            lore(
                listOf(
                    MiniMessage.miniMessage().deserialize("<gold><bold>여름 <orange>가을</bold><gray> 작물"),
                    MiniMessage.miniMessage().deserialize("<gray>재배까지 총 4일 소요")
                )
            )
        }
        return itemStack
    }

    override fun getHarvestItem(): ItemStack {
        val itemStack = ItemStack(Material.WHEAT_SEEDS, 1)
        itemStack.itemMeta.run {
            displayName(Component.text("밀"))
        }
        return itemStack
    }
}