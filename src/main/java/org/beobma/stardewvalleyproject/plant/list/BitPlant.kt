package org.beobma.stardewvalleyproject.plant.list

import org.beobma.stardewvalleyproject.plant.EatablePlants
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.potion.PotionEffect

class BitPlant : Plant("비트", 4, 4, 4, listOf(Season.Autumn)), EatablePlants {
    override val silverNutrition: Int = 8
    override val goldNutrition: Int = 10
    override val titaniumNutrition: Int = 12

    override val silverSaturation: Float = 8f
    override val goldSaturation: Float = 9f
    override val titaniumSaturation: Float = 10f

    override val silverConsumeSeconds: Float = 3f
    override val goldConsumeSeconds: Float = 2.5f
    override val titaniumConsumeSeconds: Float = 2f

    override val silverEffects: List<PotionEffect> = listOf()
    override val goldEffects: List<PotionEffect> = listOf()
    override val titaniumEffects: List<PotionEffect> = listOf()
}