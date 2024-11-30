package org.beobma.stardewvalleyproject.tool

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Hoe {
    val hoe = ItemStack(Material.PAPER, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(MiniMessage.miniMessage().deserialize("괭이"))
            lore(
                listOf(MiniMessage.miniMessage().deserialize("<gray>땅을 갈 수 있는 괭이"))
            )
        }
    }
    val durableHoe = ItemStack(Material.PAPER, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(MiniMessage.miniMessage().deserialize("단단한 괭이"))
            lore(
                listOf(MiniMessage.miniMessage().deserialize("<gray>땅을 갈 수 있는 괭이"))
            )
        }
    }
    val lightAndSturdyHoe = ItemStack(Material.PAPER, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(MiniMessage.miniMessage().deserialize("가볍고 단단한 괭이"))
            lore(
                listOf(
                    MiniMessage.miniMessage().deserialize("<gray>땅을 갈 수 있는 괭이")
                )
            )
        }
    }
    val autoHoe = ItemStack(Material.PAPER, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(MiniMessage.miniMessage().deserialize("자동화 괭이"))
            lore(
                listOf(
                    MiniMessage.miniMessage().deserialize("<gray>땅을 갈 수 있는 괭이"),
                    MiniMessage.miniMessage().deserialize("<gray>왼손에 들고있는 씨앗을 자동으로 심는다.")
                )
            )
        }
    }

    val hoes = hashSetOf<ItemStack>(hoe, durableHoe, lightAndSturdyHoe, autoHoe)
}