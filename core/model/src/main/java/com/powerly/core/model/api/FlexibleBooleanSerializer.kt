package com.powerly.core.model.api

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull

object FlexibleBooleanSerializer : KSerializer<Boolean> {
    override val descriptor = PrimitiveSerialDescriptor("FlexibleBoolean", PrimitiveKind.BOOLEAN)

    override fun serialize(encoder: Encoder, value: Boolean) = encoder.encodeBoolean(value)

    override fun deserialize(decoder: Decoder): Boolean {
        return when (val input = (decoder as? JsonDecoder)?.decodeJsonElement()) {
            is JsonPrimitive -> when {
                input.isString -> input.content.equals("true", true) || input.content == "1"
                input.intOrNull == 1 -> true
                input.intOrNull == 0 -> false
                input.booleanOrNull != null -> input.boolean
                else -> false
            }

            else -> false
        }
    }
}

object FlexibleIntSerializer : KSerializer<Int> {
    override val descriptor = PrimitiveSerialDescriptor("FlexibleInt", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Int) = encoder.encodeInt(value)

    override fun deserialize(decoder: Decoder): Int {
        return when (val input = (decoder as? JsonDecoder)?.decodeJsonElement()) {
            is JsonPrimitive -> when {
                input.isString -> input.content.toIntOrNull() ?: 0
                input.intOrNull != null -> input.int
                else -> 0
            }

            else -> 0
        }
    }
}

object FlexibleDoubleSerializer : KSerializer<Double> {
    override val descriptor = PrimitiveSerialDescriptor("FlexibleDouble", PrimitiveKind.DOUBLE)

    override fun serialize(encoder: Encoder, value: Double) = encoder.encodeDouble(value)

    override fun deserialize(decoder: Decoder): Double {
        return when (val input = (decoder as? JsonDecoder)?.decodeJsonElement()) {
            is JsonPrimitive -> when {
                input.isString -> input.content.toDoubleOrNull() ?: 0.0
                input.doubleOrNull != null -> input.double
                else -> 0.0
            }

            else -> 0.0
        }
    }
}
