package org.beobma.stardewvalleyproject.plant.list

import org.beobma.stardewvalleyproject.plant.EatablePlants
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.potion.PotionEffect

class PotatoPlant : Plant("감자", 3, 3, 6, listOf(Season.Spring)), EatablePlants {
    override val silverNutrition: Int = 3
    override val goldNutrition: Int = 4
    override val titaniumNutrition: Int = 5

    override val silverSaturation: Float = 2f
    override val goldSaturation: Float = 4f
    override val titaniumSaturation: Float = 6f

    override val silverConsumeSeconds: Float = 1.6f
    override val goldConsumeSeconds: Float = 1.4f
    override val titaniumConsumeSeconds: Float = 1.25f

    override val silverEffects: List<PotionEffect> = listOf()
    override val goldEffects: List<PotionEffect> = listOf()
    override val titaniumEffects: List<PotionEffect> = listOf()
}