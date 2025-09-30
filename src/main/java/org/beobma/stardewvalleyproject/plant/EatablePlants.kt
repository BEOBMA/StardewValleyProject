package org.beobma.stardewvalleyproject.plant

import org.bukkit.potion.PotionEffect

interface EatablePlants {
    val silverNutrition: Int
    val goldNutrition: Int
    val titaniumNutrition: Int

    val silverSaturation: Float
    val goldSaturation: Float
    val titaniumSaturation: Float

    val silverConsumeSeconds: Float
    val goldConsumeSeconds: Float
    val titaniumConsumeSeconds: Float

    val silverEffects: List<PotionEffect>
    val goldEffects: List<PotionEffect>
    val titaniumEffects: List<PotionEffect>
}