package com.krossovochkin.serialization

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

object FiberyJson {

    val json: Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = true
        encodeDefaults = true
        coerceInputValues = true
        // Stored filters use "type" as the discriminator key, keep it.
        classDiscriminator = "type"
        serializersModule = SerializersModule {
            contextual(Any::class, AnySerializer)
        }
    }
}
