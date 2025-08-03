package org.beobma.stardewvalleyproject.resource

import kotlinx.serialization.Serializable
import org.beobma.stardewvalleyproject.data.LocationSerializer
import org.bukkit.Location

@Serializable
data class Resource(
    val resourcesType: ResourceType,
    @Serializable(with = LocationSerializer::class)
    val location: Location,
    var isGathering: Boolean = false,
    var uuidString: String? = null
)