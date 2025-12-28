package org.beobma.prccore.manager

import org.bukkit.inventory.ItemStack

object CustomModelDataManager {
    fun ItemStack.getCustomModelData(): Int {
        val meta = itemMeta ?: return 0
        if (!meta.hasCustomModelData()) return 0
        return meta.customModelData
    }
}