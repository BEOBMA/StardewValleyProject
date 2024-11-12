package org.beobma.stardewvalleyproject.manager

import org.beobma.stardewvalleyproject.resources.Resources
import org.bukkit.entity.Player

interface MineHandler {
    fun reset()
    fun Player.gathering(resources: Resources)
}

object MineManager : MineHandler {
    override fun reset() {
        // 하루가 넘어가거나, 기타 이유로 광산 내부를 리셋해야할 경우
    }

    override fun Player.gathering(resources: Resources) {
        // 자원을 제거하는 등의 기능이 필요
        inventory.addItem(resources.forage)
    }
}