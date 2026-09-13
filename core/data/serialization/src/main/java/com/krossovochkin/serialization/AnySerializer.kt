package com.krossovochkin.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
object AnySerializer : KSerializer<Any> {

    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("Any", SerialKind.CONTEXTUAL)

    override fun serialize(encoder: Encoder, value: Any) {
        require(encoder is JsonEncoder) { "AnySerializer supports JSON only" }
        @Suppress("SENSELESS_COMPARISON")
        if (value as Any? == null) {
            // Defensive: a non-null `Any` slot can hold null at runtime
            // (e.g. JSON null decoded into `Map<String, Any>`, mirroring Moshi).
            encoder.encodeJsonElement(JsonNull)
            return
        }
        encoder.encodeJsonElement((value as Any?).toJsonElement(encoder))
    }

    override fun deserialize(decoder: Decoder): Any {
        require(decoder is JsonDecoder) { "AnySerializer supports JSON only" }
        @Suppress("UNCHECKED_CAST")
        return decoder.decodeJsonElement().toAny() as Any
    }

    private fun Any?.toJsonElement(encoder: JsonEncoder): JsonElement {
        return when (this) {
            null -> JsonNull
            is JsonElement -> this
            is String -> JsonPrimitive(this)
            is Boolean -> JsonPrimitive(this)
            is Number -> JsonPrimitive(toJsonNumber())
            is Map<*, *> -> buildJsonObject {
                entries.forEach { (key, value) ->
                    put(key.toString(), value.toJsonElement(encoder))
                }
            }
            is Iterable<*> -> buildJsonArray {
                this@toJsonElement.forEach { add(it.toJsonElement(encoder)) }
            }
            is Array<*> -> buildJsonArray {
                this@toJsonElement.forEach { add(it.toJsonElement(encoder)) }
            }
            else -> {
                @Suppress("UNCHECKED_CAST")
                val kClass = this::class as KClass<Any>
                val serializer = kClass.serializer() as KSerializer<Any?>
                encoder.json.encodeToJsonElement(serializer, this)
            }
        }
    }

    private fun Number.toJsonNumber(): Number {
        return when (this) {
            is Int, is Long, is Short, is Byte -> toLong()
            is Float, is Double -> toDouble()
            else -> this
        }
    }

    private fun JsonElement.toAny(): Any? {
        return when (this) {
            is JsonNull -> null
            is JsonObject -> entries.associate { (key, element) -> key to element.toAny() }
            is JsonArray -> map { it.toAny() }
            is JsonPrimitive -> {
                when {
                    isString -> content
                    booleanOrNull != null -> booleanOrNull
                    else -> content.toLongOrNull() ?: content.toDoubleOrNull() ?: content
                }
            }
        }
    }
}
