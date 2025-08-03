package org.beobma.stardewvalleyproject.mine

import kotlinx.serialization.Serializable
import org.beobma.stardewvalleyproject.data.LocationSerializer
import org.beobma.stardewvalleyproject.entity.Enemy
import org.beobma.stardewvalleyproject.resource.Resource
import org.bukkit.Location
import org.bukkit.entity.Player

@Serializable
data class Mine(
    val floor: Int,
    val mineTemplate: MineTemplate,
    val mineType: MineType,
    val resources: MutableList<Resource> = mutableListOf(),
    val enemys: MutableList<Enemy> = mutableListOf(),
    val players: MutableList<Player> = mutableListOf(),
    @Serializable(with = LocationSerializer::class)
    var startBlockLocation: Location? = null,
    @Serializable(with = LocationSerializer::class)
    var exitBlockLocation: Location? = null,
)