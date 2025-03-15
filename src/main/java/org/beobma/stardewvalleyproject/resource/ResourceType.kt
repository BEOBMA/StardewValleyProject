package org.beobma.stardewvalleyproject.resource

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

enum class ResourceType(val dropItem: ItemStack, val material: Material) {
    Magnesium(ItemStack(Material.AIR), Material.AIR),
    Aluminum(ItemStack(Material.AIR), Material.AIR),
    Iron(ItemStack(Material.AIR), Material.AIR),
    Copper(ItemStack(Material.AIR), Material.AIR),
    Lithium(ItemStack(Material.AIR), Material.AIR),
    Gold(ItemStack(Material.AIR), Material.AIR),
    Platinum(ItemStack(Material.AIR), Material.AIR),
    Nickel(ItemStack(Material.AIR), Material.AIR),
    Titanium(ItemStack(Material.AIR), Material.AIR)
}