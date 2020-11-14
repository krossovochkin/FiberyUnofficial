package by.krossovochkin.fiberyunofficial.core.data.serialization

interface Serializer {

    fun <T> listToJson(data: List<T>, clazz: Class<T>): String

    fun <K, V> mapToJson(data: Map<K, V>, keyClass: Class<K>, valueClass: Class<V>): String

    fun <T> objToJson(data: T): String

    fun <T> jsonToList(json: String, clazz: Class<T>): List<T>

    fun <K, V> jsonToMap(json: String, keyClass: Class<K>, valueClass: Class<V>): Map<K, V>

    fun <T> jsonToObj(json: String): T
}
