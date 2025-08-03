package org.beobma.stardewvalleyproject.manager

import org.beobma.stardewvalleyproject.StardewValley
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.persistence.PersistentDataType

object ToolManager {
    private val instance = StardewValley.instance

    val HOE_CUSTOM_MODEL_DATAS: IntArray = intArrayOf(
        DURABLE_HOE_CUSTOM_MODEL_DATA,
        LIGHT_AND_STURDY_HOE_CUSTOM_MODEL_DATA,
        AUTO_HOE_CUSTOM_MODEL_DATA
    )
    val WATERINGCAN_CUSTOM_MODEL_DATAS: IntArray = intArrayOf(
        WATERINGCAN_CUSTOM_MODEL_DATA,
        PUMP_WATERINGCAN_CUSTOM_MODEL_DATA
    )
    val CAPSULE_MODEL_DATAS: IntArray = intArrayOf(
        WEED_KILLER_CAPSULE_MODEL_DATA,
        GROWTH_CAPSULE_MODEL_DATA,
        NUTRIENT_CAPSULE_MODEL_DATA
    )
    val PICKAXE_MODEL_DATAS: IntArray = intArrayOf(
        PICKAXE_MODEL_DATA,
        LIGHTDRILL_MODEL_DATA,
        HEAVYDRILL_MODEL_DATA
    )

    const val DURABLE_HOE_CUSTOM_MODEL_DATA: Int = 4
    const val LIGHT_AND_STURDY_HOE_CUSTOM_MODEL_DATA: Int = 5
    const val AUTO_HOE_CUSTOM_MODEL_DATA: Int = 6

    const val WATERINGCAN_CUSTOM_MODEL_DATA: Int = 7
    const val PUMP_WATERINGCAN_CUSTOM_MODEL_DATA: Int = 8

    const val CAPSULEGUN_CUSTOM_MODEL_DATA: Int = 9
    const val WEED_KILLER_CAPSULE_MODEL_DATA: Int = 38
    const val GROWTH_CAPSULE_MODEL_DATA: Int = 39
    const val NUTRIENT_CAPSULE_MODEL_DATA: Int = 40

    const val PICKAXE_MODEL_DATA: Int = 1
    const val LIGHTDRILL_MODEL_DATA: Int = 2
    const val HEAVYDRILL_MODEL_DATA: Int = 3

    private val KEY_MAX = NamespacedKey(instance, "custom_durability_max")
    private val KEY_CUR = NamespacedKey(instance, "custom_durability_cur")

    fun ItemStack.setMaxCustomDurability(max: Int) {
        val meta = itemMeta ?: return
        if (meta !is Damageable) return
        val container = meta.persistentDataContainer

        container.set(KEY_MAX, PersistentDataType.INTEGER, max)
        container.set(KEY_CUR, PersistentDataType.INTEGER, max)
        itemMeta = meta
    }

    fun ItemStack.getMaxCustomDurability(): Int? {
        val meta = itemMeta ?: return null
        val container = meta.persistentDataContainer
        return container.get(KEY_MAX, PersistentDataType.INTEGER)
    }

    fun ItemStack.getCurrentCustomDurability(): Int? {
        val meta = itemMeta ?: return null
        val container = meta.persistentDataContainer
        return container.get(KEY_CUR, PersistentDataType.INTEGER)
    }

    fun ItemStack.decreaseCustomDurability(amount: Int, player: Player) {
        val meta = itemMeta ?: return
        if (meta !is Damageable) return
        val container = meta.persistentDataContainer

        val max = container.get(KEY_MAX, PersistentDataType.INTEGER) ?: return
        val current = container.get(KEY_CUR, PersistentDataType.INTEGER) ?: return

        val newDurability = (current - amount).coerceAtLeast(0)
        container.set(KEY_CUR, PersistentDataType.INTEGER, newDurability)

        val vanillaMax = type.maxDurability.toInt()
        val visualDamage = ((1.0 - newDurability.toDouble() / max.toDouble()) * vanillaMax).toInt()
        meta.damage = visualDamage.coerceAtMost(vanillaMax)


        itemMeta = meta

        if (newDurability <= 0) {
            destruction(player)
        }
    }


    private fun ItemStack.destruction(player: Player) {
        player.inventory.remove(this)
    }
}