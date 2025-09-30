package org.beobma.stardewvalleyproject.plant.list

import org.beobma.stardewvalleyproject.plant.EatablePlants
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class CucumberPlant : Plant("오이", 4, 4, 5, listOf(Season.Spring)), EatablePlants {
    override val silverNutrition: Int = 5
    override val goldNutrition: Int = 6
    override val titaniumNutrition: Int = 7

    override val silverSaturation: Float = 4f
    override val goldSaturation: Float = 6f
    override val titaniumSaturation: Float = 8f

    override val silverConsumeSeconds: Float = 1.6f
    override val goldConsumeSeconds: Float = 1.4f
    override val titaniumConsumeSeconds: Float = 1.25f

    override val silverEffects: List<PotionEffect> = listOf()
    override val goldEffects: List<PotionEffect> = listOf()
    override val titaniumEffects: List<PotionEffect> = listOf(PotionEffect(PotionEffectType.LUCK, 10, 0, true, false, true))
}