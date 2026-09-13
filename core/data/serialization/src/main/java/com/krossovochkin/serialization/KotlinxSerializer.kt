package com.krossovochkin.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
class KotlinxSerializer(
    val json: Json = FiberyJson.json,
) : Serializer {

    override fun <T> listToJson(data: List<T>, clazz: Class<T>): String {
        @Suppress("UNCHECKED_CAST")
        return json.encodeToString(ListSerializer(serializerFor(clazz)) as KSerializer<List<T>>, data)
    }

    override fun <K, V> mapToJson(data: Map<K, V>, keyClass: Class<K>, valueClass: Class<V>): String {
        @Suppress("UNCHECKED_CAST")
        return json.encodeToString(
            MapSerializer(serializerFor(keyClass), serializerFor(valueClass)) as KSerializer<Map<K, V>>,
            data,
        )
    }

    override fun <T> objToJson(data: T, clazz: Class<T>): String {
        @Suppress("UNCHECKED_CAST")
        return json.encodeToString(serializerFor(clazz) as KSerializer<T>, data as T)
    }

    override fun <T> polymorphicObjToJson(
        data: T,
        clazz: Class<T>,
        polymorphicData: List<Serializer.PolymorphicData<*>>,
    ): String {
        return objToJson(data, clazz)
    }

    override fun <T> jsonToList(json: String, clazz: Class<T>): List<T> {
        return this.json.decodeFromString(ListSerializer(serializerFor(clazz)), json)
    }

    override fun <K, V> jsonToMap(json: String, keyClass: Class<K>, valueClass: Class<V>): Map<K, V> {
        return this.json.decodeFromString(MapSerializer(serializerFor(keyClass), serializerFor(valueClass)), json)
    }

    override fun <T> jsonToObj(json: String, clazz: Class<T>): T? {
        if (json.isEmpty()) return null
        return runCatching {
            this.json.decodeFromString(serializerFor(clazz), json)
        }.getOrNull()
    }

    override fun <T> jsonToPolymorphicObj(
        json: String,
        clazz: Class<T>,
        polymorphicData: List<Serializer.PolymorphicData<*>>,
    ): T? {
        return jsonToObj(json, clazz)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> serializerFor(clazz: Class<T>): KSerializer<T> {
        if (clazz == Any::class.java || clazz == Object::class.java) {
            return AnySerializer as KSerializer<T>
        }
        val kClass = (clazz as Class<Any>).kotlin as KClass<Any>
        return kClass.serializer() as KSerializer<T>
    }
}
