package com.krossovochkin.serialization

interface Serializer {

    fun <T> listToJson(data: List<T>, clazz: Class<T>): String

    fun <K, V> mapToJson(data: Map<K, V>, keyClass: Class<K>, valueClass: Class<V>): String

    fun <T> objToJson(data: T, clazz: Class<T>): String

    fun <T> polymorphicObjToJson(
        data: T,
        clazz: Class<T>,
        polymorphicData: List<PolymorphicData<*>>
    ): String

    fun <T> jsonToList(json: String, clazz: Class<T>): List<T>

    fun <K, V> jsonToMap(json: String, keyClass: Class<K>, valueClass: Class<V>): Map<K, V>

    fun <T> jsonToObj(json: String, clazz: Class<T>): T?

    fun <T> jsonToPolymorphicObj(
        json: String,
        clazz: Class<T>,
        polymorphicData: List<PolymorphicData<*>>
    ): T?

    data class PolymorphicData<T>(
        val clazz: Class<T>,
        val key: String,
        val subTypeMap: Map<Class<out T>, String>
    )
}
