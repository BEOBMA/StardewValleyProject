package org.beobma.stardewvalleyproject.tool

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Hoe {
    val hoe = ItemStack(Material.STONE_HOE, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(MiniMessage.miniMessage().deserialize("괭이"))
            lore(
                listOf(MiniMessage.miniMessage().deserialize("<gray>땅을 갈 수 있는 괭이"))
            )
        }
    }
    val durableHoe = ItemStack(Material.IRON_HOE, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(MiniMessage.miniMessage().deserialize("단단한 괭이"))
            lore(
                listOf(MiniMessage.miniMessage().deserialize("<gray>땅을 갈 수 있는 괭이. 조금 더 튼튼하다."))
            )

        }
    }
    val lightAndSturdyHoe = ItemStack(Material.DIAMOND_HOE, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(MiniMessage.miniMessage().deserialize("가볍고 단단한 괭이"))
            lore(
                listOf(
                    MiniMessage.miniMessage().deserialize("<gray>땅을 갈 수 있는 괭이. 한번에 더 큰 면적을 갈 수 있다.")
                )
            )
        }
    }
    val autoHoe = ItemStack(Material.NETHERITE_HOE, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(MiniMessage.miniMessage().deserialize("자동화 괭이"))
            lore(
                listOf(
                    MiniMessage.miniMessage().deserialize("<gray>땅을 갈 수 있는 괭이. 한번에 더 큰 면적을 갈 수 있다."),
                    MiniMessage.miniMessage().deserialize("<gray>왼손에 들고있는 씨앗을 자동으로 심는다.")
                )
            )
        }
    }

    val hoes = hashSetOf<ItemStack>(hoe, durableHoe, lightAndSturdyHoe, autoHoe)
}