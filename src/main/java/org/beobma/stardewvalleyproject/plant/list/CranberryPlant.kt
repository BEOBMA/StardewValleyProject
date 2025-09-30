package org.beobma.stardewvalleyproject.plant.list

import org.beobma.stardewvalleyproject.plant.EatablePlants
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.potion.PotionEffect

class CranberryPlant : Plant("크랜베리", 6, 6, 5, listOf(Season.Autumn)), EatablePlants {
    override val silverNutrition: Int = 2
    override val goldNutrition: Int = 2
    override val titaniumNutrition: Int = 2

    override val silverSaturation: Float = 0.4f
    override val goldSaturation: Float = 0.6f
    override val titaniumSaturation: Float = 0.8f

    override val silverConsumeSeconds: Float = 0.8f
    override val goldConsumeSeconds: Float = 0.75f
    override val titaniumConsumeSeconds: Float = 0.7f

    override val silverEffects: List<PotionEffect> = listOf()
    override val goldEffects: List<PotionEffect> = listOf()
    override val titaniumEffects: List<PotionEffect> = listOf()
}