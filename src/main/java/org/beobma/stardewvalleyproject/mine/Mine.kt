package org.beobma.stardewvalleyproject.mine

import org.beobma.stardewvalleyproject.resource.Resource
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

data class Mine(
    val floor: Int,
    val mineTemplate: MineTemplate,
    val mineType: MineType,
    val startLocation: Location,
    val resources: MutableList<Resource> = mutableListOf(),
    val enemys: MutableList<Entity> = mutableListOf(),
    val players: MutableList<Player> = mutableListOf(),
    var startBlock: Block? = null,
    var exitBlock: Block? = null,
)