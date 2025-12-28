package org.beobma.prccore.listener

import org.beobma.prccore.manager.DataManager.gameData
import org.beobma.prccore.manager.DataManager.playerList
import org.beobma.prccore.manager.MineManager.showMineFloorSelector
import org.beobma.prccore.manager.PlantManager.getRegisterPlants
import org.beobma.prccore.manager.PlantManager.getSeedItem
import org.beobma.prccore.manager.TimeManager.showTimeBossBar
import org.beobma.prccore.manager.TimeManager.timePlay
import org.beobma.prccore.tool.Capsule
import org.beobma.prccore.tool.Hoe
import org.beobma.prccore.tool.Pickaxe
import org.beobma.prccore.tool.WateringCan
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent


class OnPlayerJoin : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        playerList.add(player)
        gameData.maxMineFloor = 60
        showTimeBossBar(player)
        timePlay()
        player.inventory.run {
            Hoe().hoes.forEach {
                addItem(it)
            }

            val capsule = Capsule()
            addItem(capsule.capsuleGun)
            capsule.capsules.forEach {
                addItem(it)
            }

            WateringCan().wateringCans.forEach {
                addItem(it)
            }

            Pickaxe().pickaxes.forEach {
                addItem(it)
            }

            getRegisterPlants().forEach {
                addItem(it.getSeedItem())
            }
        }
        showMineFloorSelector(player)
    }
}