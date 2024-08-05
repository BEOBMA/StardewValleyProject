package org.beobma.stardewvalleyproject.serializer

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.inventory.ItemStack

class ItemStackSerializer : KSerializer<ItemStack> {
    private val gson: Gson = GsonBuilder().create()

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ItemStack", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ItemStack) {
        val jsonString = gson.toJson(value)
        encoder.encodeString(jsonString)
    }

    override fun deserialize(decoder: Decoder): ItemStack {
        val jsonString = decoder.decodeString()
        return gson.fromJson(jsonString, ItemStack::class.java)
    }
}