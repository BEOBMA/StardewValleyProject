package org.beobma.stardewvalleyproject.plant.list

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class BitPlant : Plant("비트", 4, 4, listOf(Season.Autumn)) {
    override fun getSeedItem(): ItemStack {
        val itemStack = ItemStack(Material.WHEAT_SEEDS, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("비트 씨앗"))
                lore(
                    listOf(
                        MiniMessage.miniMessage().deserialize("<yellow><bold>가을 작물"),
                        MiniMessage.miniMessage().deserialize("<gray>재배까지 총 4일 소요")
                    )
                )
            }
        }


        return itemStack
    }

    override fun getHarvestItem(): ItemStack {
        val itemStack = ItemStack(Material.WHEAT_SEEDS, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("비트"))
            }
        }

        return itemStack
    }

    override fun copy(): Plant {
        return BitPlant()
    }
}