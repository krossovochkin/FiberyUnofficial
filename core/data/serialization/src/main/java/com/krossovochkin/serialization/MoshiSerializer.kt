package com.krossovochkin.serialization

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class MoshiSerializer(
    private val moshi: Moshi
) : Serializer {

    override fun <T> listToJson(data: List<T>, clazz: Class<T>): String {
        return moshi
            .adapter<List<T>>(
                Types.newParameterizedType(List::class.java, clazz)
            )
            .toJson(data)
            .orEmpty()
    }

    override fun <K, V> mapToJson(
        data: Map<K, V>,
        keyClass: Class<K>,
        valueClass: Class<V>
    ): String {
        TODO("Not yet implemented")
    }

    override fun <T> objToJson(data: T): String {
        TODO("Not yet implemented")
    }

    override fun <T> jsonToList(json: String, clazz: Class<T>): List<T> {
        return moshi
            .adapter<List<T>>(
                Types.newParameterizedType(List::class.java, clazz)
            )
            .fromJson(json)
            .orEmpty()
    }

    override fun <K, V> jsonToMap(
        json: String,
        keyClass: Class<K>,
        valueClass: Class<V>
    ): Map<K, V> {
        return moshi
            .adapter<Map<K, V>>(
                Types.newParameterizedType(
                    Map::class.java,
                    keyClass,
                    valueClass
                )
            )
            .fromJson(json)
            .orEmpty()
    }

    override fun <T> jsonToObj(json: String): T {
        TODO("Not yet implemented")
    }
}
