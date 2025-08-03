package org.beobma.stardewvalleyproject.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import org.bukkit.Bukkit
import org.bukkit.Location

object LocationSerializer : KSerializer<Location> {
    private val world = Bukkit.getWorlds().first()

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Location") {
        element("x", PrimitiveSerialDescriptor("x", PrimitiveKind.DOUBLE))
        element("y", PrimitiveSerialDescriptor("y", PrimitiveKind.DOUBLE))
        element("z", PrimitiveSerialDescriptor("z", PrimitiveKind.DOUBLE))
    }

    override fun serialize(encoder: Encoder, value: Location) {
        encoder.encodeStructure(descriptor) {
            encodeDoubleElement(descriptor, 0, value.x)
            encodeDoubleElement(descriptor, 1, value.y)
            encodeDoubleElement(descriptor, 2, value.z)
        }
    }

    override fun deserialize(decoder: Decoder): Location {
        var x = 0.0
        var y = 0.0
        var z = 0.0

        decoder.decodeStructure(descriptor) {
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> x = decodeDoubleElement(descriptor, index)
                    1 -> y = decodeDoubleElement(descriptor, index)
                    2 -> z = decodeDoubleElement(descriptor, index)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
        }

        return Location(world, x, y, z, 0.0f, 0.0f)
    }
}

object LocationListSerializer : KSerializer<List<Location>> {
    private val delegate = ListSerializer(LocationSerializer)

    override val descriptor = delegate.descriptor

    override fun serialize(encoder: Encoder, value: List<Location>) {
        delegate.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): List<Location> {
        return delegate.deserialize(decoder)
    }
}