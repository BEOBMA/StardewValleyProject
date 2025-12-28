package org.beobma.prccore.entity

import kotlinx.serialization.Serializable
import org.beobma.prccore.data.LocationSerializer
import org.bukkit.Location
import org.bukkit.entity.EntityType

@Serializable
data class Enemy(
    @Serializable(with = LocationSerializer::class)
    val location: Location,
    val entityType: EntityType,
    var isSpawn: Boolean = false,
    var isDead: Boolean = false,
    var enemyUUID: String? = null
)