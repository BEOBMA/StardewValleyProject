package org.beobma.stardewvalleyproject.data

import kotlinx.serialization.Serializable

@Serializable
data class AdvancementData(
    val playerUUID: String,
    val advancementString: String,
    var value: Int
)
