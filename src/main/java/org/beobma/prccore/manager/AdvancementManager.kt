package org.beobma.prccore.manager

import org.beobma.prccore.data.AdvancementData
import org.beobma.prccore.manager.DataManager.advancementList
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player


object AdvancementManager {
    fun grantAdvancement(player: Player, adv: String): Boolean {
        val key = NamespacedKey("pcadv", adv)
        val advancement = Bukkit.getAdvancement(key)

        if (advancement != null && !player.getAdvancementProgress(advancement).isDone) {
            val progress = player.getAdvancementProgress(advancement)

            for (criteria in progress.remainingCriteria) {
                progress.awardCriteria(criteria)
            }
            return true
        }
        return false
    }

    // 함수 실행 시, 도전과제 value에 1을 더함, maxValue보다 커지면 자동으로 도전과제 완료 처리
    fun addAdvancementInt(player: Player, adv: String, maxValue: Int) {
        val advancementData = advancementList.find { it.playerUUID == player.uniqueId.toString() }

        if (advancementData == null) {
            val newData = AdvancementData(player.uniqueId.toString(), adv, 1)
            advancementList.add(newData)

            if (newData.value >= maxValue) {
                grantAdvancement(player, adv)
                return
            }
        }
        else {
            advancementData.value += 1

            if (advancementData.value >= maxValue) {
                grantAdvancement(player, adv)
                return
            }
        }
    }
}