package org.beobma.stardewvalleyproject.tool

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Capsule {
    val capsuleGun = ItemStack(Material.PAPER, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(MiniMessage.miniMessage().deserialize("캡슐건"))
            lore(
                listOf(
                    MiniMessage.miniMessage().deserialize("<gray>왼손에 든 캡슐을 소모하여 작물에 사용한다. 한 작물에 여러 캡슐을 사용할 수는 없다.")
                )
            )
        }
    }

    val weedKillerCapsule = ItemStack(Material.PAPER, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(MiniMessage.miniMessage().deserialize("제초 캡슐"))
            lore(
                listOf(MiniMessage.miniMessage().deserialize("<gray>제초제 성분이 담긴 캡슐, 뿌리면 N일 동안은 경작지에서 잡초가 자라지 않는다."))
            )
        }
    }

    val growthCapsule = ItemStack(Material.PAPER, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(MiniMessage.miniMessage().deserialize("성장 캡슐"))
            lore(
                listOf(MiniMessage.miniMessage().deserialize("<gray>성장 촉진제 성분이 담긴 캡슐, 수확일 까지 시간을 줄여준다."))
            )
        }
    }

    val nutrientCapsule = ItemStack(Material.PAPER, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(MiniMessage.miniMessage().deserialize("영양 캡슐"))
            lore(
                listOf(MiniMessage.miniMessage().deserialize("<gray>비료 성분이 담긴 캡슐, 더 높은 등급의 작물이 나올 확률이 증가한다."))
            )
        }
    }

    val capsules = hashSetOf<ItemStack>(weedKillerCapsule, nutrientCapsule, growthCapsule)
}