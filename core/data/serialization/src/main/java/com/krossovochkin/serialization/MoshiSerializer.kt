package com.krossovochkin.serialization

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory

class MoshiSerializer(
    val moshi: Moshi
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

    override fun <T> objToJson(data: T, clazz: Class<T>): String {
        return moshi.adapter(clazz).toJson(data)
    }

    override fun <T> polymorphicObjToJson(
        data: T,
        clazz: Class<T>,
        polymorphicData: List<Serializer.PolymorphicData<*>>
    ): String {
        return createPolymorphicMoshi(polymorphicData)
            .adapter(clazz)
            .toJson(data)
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

    override fun <T> jsonToObj(json: String, clazz: Class<T>): T? {
        return moshi.adapter(clazz).fromJson(json)
    }

    override fun <T> jsonToPolymorphicObj(
        json: String,
        clazz: Class<T>,
        polymorphicData: List<Serializer.PolymorphicData<*>>
    ): T? {
        return createPolymorphicMoshi(polymorphicData)
            .adapter(clazz)
            .fromJson(json)
    }

    private fun createPolymorphicMoshi(
        polymorphicData: List<Serializer.PolymorphicData<*>>
    ): Moshi {
        var moshi = moshi
        polymorphicData.forEach {
            moshi = createPolymorphicMoshi(moshi, it)
        }
        return moshi
    }

    private fun <T> createPolymorphicMoshi(
        moshi: Moshi,
        polymorphicData: Serializer.PolymorphicData<T>
    ): Moshi {
        var factory = PolymorphicJsonAdapterFactory.of(polymorphicData.clazz, polymorphicData.key)
        polymorphicData.subTypeMap.forEach { (clazz, value) ->
            factory = factory.withSubtype(clazz, value)
        }
        return moshi.newBuilder()
            .add(factory)
            .build()
    }
}
