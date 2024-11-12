package org.beobma.stardewvalleyproject.resources

import org.bukkit.Location
import org.bukkit.inventory.ItemStack

data class Resources(
    val resourcesType: ResourcesType,
    val location: Location,
    val forage: ItemStack
)