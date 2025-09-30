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

    fun getNutrition(quality: Int): Int = when (quality) {
        QUALITY_TITANIUM -> titaniumNutrition
        QUALITY_GOLD -> goldNutrition
        else -> silverNutrition
    }

    fun getSaturation(quality: Int): Float = when (quality) {
        QUALITY_TITANIUM -> titaniumSaturation
        QUALITY_GOLD -> goldSaturation
        else -> silverSaturation
    }

    fun getConsumeSeconds(quality: Int): Float = when (quality) {
        QUALITY_TITANIUM -> titaniumConsumeSeconds
        QUALITY_GOLD -> goldConsumeSeconds
        else -> silverConsumeSeconds
    }

    fun getEffects(quality: Int): List<PotionEffect> = when (quality) {
        QUALITY_TITANIUM -> titaniumEffects
        QUALITY_GOLD -> goldEffects
        else -> silverEffects
    }

    companion object {
        const val QUALITY_SILVER = 0
        const val QUALITY_GOLD = 1
        const val QUALITY_TITANIUM = 2
    }
}