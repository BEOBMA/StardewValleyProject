package org.beobma.stardewvalleyproject.mine

import org.bukkit.Location


enum class MineType(val xInterpolation: Double, val yInterpolation: Double, val zInterpolation: Double, val resourcesLocations: List<Location>, val enemysLocations: List<Location>) {
    A(
        8.0, -21.0, 92.0,
        listOf(
            //TODO("자원 생성 위치")
        ), listOf(
            //TODO("적 생성 위치")
        )
    ),
    B(
        8.0, -22.0, 124.0,
        listOf(
            //TODO("자원 생성 위치")
        ), listOf(
            //TODO("적 생성 위치")
        )
    ),
    C(
        9.0, -19.0, 334.0,
        listOf(
            //TODO("자원 생성 위치")
        ), listOf(
            //TODO("적 생성 위치")
        )
    ),
    D(
        26.0, -28.0, 452.0,
        listOf(
            //TODO("자원 생성 위치")
        ), listOf(
            //TODO("적 생성 위치")
        )
    ),
    E(
        23.0, -23.0, 489.0,
        listOf(
            //TODO("자원 생성 위치")
        ), listOf(
            //TODO("적 생성 위치")
        )
    )
}