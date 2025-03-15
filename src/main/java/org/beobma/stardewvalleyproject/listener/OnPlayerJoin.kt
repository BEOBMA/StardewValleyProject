package org.beobma.stardewvalleyproject.listener

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import org.beobma.stardewvalleyproject.StardewValley.Companion.protocolManager
import org.beobma.stardewvalleyproject.manager.DataManager.gameData
import org.beobma.stardewvalleyproject.manager.FarmingManager.plants
import org.beobma.stardewvalleyproject.manager.TimeManager.timePlay
import org.beobma.stardewvalleyproject.tool.Capsule
import org.beobma.stardewvalleyproject.tool.Hoe
import org.beobma.stardewvalleyproject.tool.WateringCan
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import kotlin.random.Random

class OnPlayerJoin : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        gameData.players.add(player)
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

            plants.forEach {
                addItem(it.getSeedItem())
            }
        }
        timePlay()

        val location = player.location
        val entityId = Random.nextInt(1000, 10000)
        val spawnPacket: PacketContainer = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY)
        spawnPacket.modifier.writeDefaults()
        spawnPacket.integers.write(0, entityId)
        spawnPacket.entityTypeModifier.write(0, EntityType.DROWNED)
        spawnPacket.doubles.write(0, location.x)
        spawnPacket.doubles.write(1, location.y)
        spawnPacket.doubles.write(2, location.z)
        val yawByte: Byte = ((location.yaw * 256 / 360).toInt() and 0xFF).toByte()
        val pitchByte: Byte = ((location.pitch * 256 / 360).toInt() and 0xFF).toByte()
        spawnPacket.bytes.write(0, yawByte)
        spawnPacket.bytes.write(1, pitchByte)
        spawnPacket.bytes.write(2, yawByte)
        Bukkit.getOnlinePlayers().forEach { player ->
            protocolManager.sendServerPacket(player, spawnPacket)
        }
    }
}