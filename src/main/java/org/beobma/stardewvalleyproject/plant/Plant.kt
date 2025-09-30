package org.beobma.stardewvalleyproject.plant


import kotlinx.serialization.Serializable
import org.beobma.stardewvalleyproject.data.LocationSerializer
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.Location

@Serializable
open class Plant(
    val name: String,
    var remainingGrowthDays: Int,
    val growthDays: Int,
    val harvestAmount: Int,
    val growableSeasons: List<Season>,
    @Serializable(with = LocationSerializer::class)
    var farmlandLocation: Location? = null,
    val plantStatus: PlantStatus = PlantStatus(),
    var uuidString: String? = null,
    var quality: Int? = null
)