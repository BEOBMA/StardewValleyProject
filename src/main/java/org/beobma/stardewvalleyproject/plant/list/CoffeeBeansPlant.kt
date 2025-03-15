package org.beobma.stardewvalleyproject.plant.list

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class CoffeeBeansPlant : Plant("커피콩", 10, 10, listOf(Season.Spring, Season.Summer)) {
    override fun getSeedItem(): ItemStack {
        val itemStack = ItemStack(Material.WHEAT_SEEDS, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("커피콩"))
                lore(
                    listOf(
                        MiniMessage.miniMessage().deserialize("<green><bold>봄 <gold>여름<gray> 작물"),
                        MiniMessage.miniMessage().deserialize("<gray>재배까지 총 5일 소요")
                    )
                )
            }
        }
        return itemStack
    }

    override fun getHarvestItem(): ItemStack {
        val itemStack = ItemStack(Material.WHEAT, 1)
        itemStack.itemMeta = itemStack.itemMeta.apply {
            displayName(Component.text("커피콩"))
        }
        return itemStack
    }

    override fun copy(): Plant {
        return CoffeeBeansPlant()
    }
}