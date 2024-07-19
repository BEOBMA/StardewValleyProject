@file:Suppress("DEPRECATION")

package org.beobma.stardewvalleyproject.tool

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class WateringCan {
    val wateringCan = ItemStack(Material.PAPER, 1).apply {
        itemMeta = itemMeta.apply {
            setDisplayName("물뿌리개")
            lore = arrayListOf(
                "심어진 작물에 우클릭 하면 물을 줄 수 있습니다."
            )
        }
    }
}