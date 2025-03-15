package org.beobma.stardewvalleyproject.resource

import org.bukkit.Location

data class Resource(
    val resourcesType: ResourceType,
    val location: Location,
    val isGathering: Boolean = false
)