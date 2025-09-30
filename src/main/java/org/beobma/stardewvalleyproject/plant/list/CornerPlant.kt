package org.beobma.stardewvalleyproject.plant.list

import org.beobma.stardewvalleyproject.plant.EatablePlants
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.potion.PotionEffect

class CornerPlant : Plant("옥수수", 5, 5, 3, listOf(Season.Summer)), EatablePlants {
    override val silverNutrition: Int = 5
    override val goldNutrition: Int = 6
    override val titaniumNutrition: Int = 7

    override val silverSaturation: Float = 4f
    override val goldSaturation: Float = 6f
    override val titaniumSaturation: Float = 8f

    override val silverConsumeSeconds: Float = 2f
    override val goldConsumeSeconds: Float = 1.8f
    override val titaniumConsumeSeconds: Float = 1.6f

    override val silverEffects: List<PotionEffect> = listOf()
    override val goldEffects: List<PotionEffect> = listOf()
    override val titaniumEffects: List<PotionEffect> = listOf()
}