package org.beobma.stardewvalleyproject.listener

import org.beobma.stardewvalleyproject.manager.DataManager.playerList
import org.beobma.stardewvalleyproject.manager.PlantManager.getRegisterPlants
import org.beobma.stardewvalleyproject.manager.PlantManager.getSeedItem
import org.beobma.stardewvalleyproject.manager.TimeManager.timePlay
import org.beobma.stardewvalleyproject.tool.Capsule
import org.beobma.stardewvalleyproject.tool.Hoe
import org.beobma.stardewvalleyproject.tool.Pickaxe
import org.beobma.stardewvalleyproject.tool.WateringCan
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class OnPlayerJoin : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        playerList.add(player)
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
    }
}