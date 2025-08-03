package org.beobma.stardewvalleyproject.manager

import org.beobma.stardewvalleyproject.manager.UtilManager.miniMessage
import org.beobma.stardewvalleyproject.plant.Plant
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.ItemDisplay
import org.bukkit.inventory.ItemStack
import java.util.*

object PlantManager {
    // OFFSET은 plantFactories의 size와 같으나, 예외가 있을 수 있음.
    const val PLANT_STAR_ICON_OFFSET: Int = 10

    private val plantFactories: MutableMap<Plant, () -> Plant> = mutableMapOf()
    val plantSeedIcons: MutableMap<Plant, Int> = mutableMapOf()
    val plantAgIcons: MutableMap<Plant, Int> = mutableMapOf()
    val plantModels: MutableMap<Plant, Int> = mutableMapOf()

    fun Plant.register(clazz: Class<out Plant>, seedIconCustomModelData: Int, agIconCustomModelData: Int, modelData: Int) {
        plantFactories[this] = { clazz.getDeclaredConstructor().newInstance() }
        plantSeedIcons[this] = seedIconCustomModelData
        plantAgIcons[this] = agIconCustomModelData
        plantModels[this] = modelData
    }

    fun Plant.getSeedItem(): ItemStack {
        val registeredPlant = getRegisterPlants().find { it.name == name }
        val seedIcon = plantSeedIcons[registeredPlant]
        val seasonTexts = growableSeasons.joinToString(", ") { it.text }
        val itemStack = ItemStack(Material.BLACK_DYE, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(miniMessage.deserialize("$name 씨앗"))
                lore(
                    listOf(
                        miniMessage.deserialize("<gray>$seasonTexts 작물"),
                        miniMessage.deserialize("<gray>재배까지 총 ${remainingGrowthDays}일 소요")
                    )
                )
                setCustomModelData(seedIcon)
            }
        }
        return itemStack
    }

    fun Plant.getHarvestItem(): ItemStack {
        val itemStack = ItemStack(Material.BLACK_DYE, 1).apply {
            itemMeta = itemMeta.apply {
                displayName(miniMessage.deserialize(name))
            }
        }
        return itemStack
    }

    fun getPlantInstance(plant: Plant): Plant {
        return plantFactories[plant]!!.invoke()
    }

    fun getRegisterPlants(): List<Plant> {
        return plantFactories.map { it.key }
    }

    fun Plant.getItemDisplay(): ItemDisplay? {
        val uuid = UUID.fromString(uuidString)
        val entity = Bukkit.getEntity(uuid)
        if (entity !is ItemDisplay) return null
        return entity
    }
}