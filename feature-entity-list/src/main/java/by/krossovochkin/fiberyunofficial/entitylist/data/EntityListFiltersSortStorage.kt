package by.krossovochkin.fiberyunofficial.entitylist.data

import android.content.Context
import androidx.core.content.edit
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

interface EntityListFiltersSortStorage {

    fun setFilter(entityType: String, filter: String, params: String)

    fun setSort(entityType: String, sort: String)

    fun getFilter(entityType: String): List<Any>?

    fun getParams(entityType: String): Map<String, Any>?

    fun getSort(entityType: String): List<Any>?

    fun getRawFilter(entityType: String): String

    fun getRawParams(entityType: String): String

    fun getRawSort(entityType: String): String
}

private const val KEY_PREFS_FILTERS = "ENTITY_LIST_FILTERS"
private const val KEY_PREFS_PARAMS = "ENTITY_LIST_PARAMS"
private const val KEY_PREFS_SORT = "ENTITY_LIST_SORT"

class EntityListFiltersSortStorageImpl(
    context: Context,
    private val moshi: Moshi
) : EntityListFiltersSortStorage {

    private val filterPrefs = context.getSharedPreferences(KEY_PREFS_FILTERS, Context.MODE_PRIVATE)
    private val paramsPrefs = context.getSharedPreferences(KEY_PREFS_PARAMS, Context.MODE_PRIVATE)
    private val sortPrefs = context.getSharedPreferences(KEY_PREFS_SORT, Context.MODE_PRIVATE)

    override fun setFilter(entityType: String, filter: String, params: String) {
        filterPrefs.edit { putString(entityType, filter) }
        paramsPrefs.edit { putString(entityType, params) }
    }

    override fun setSort(entityType: String, sort: String) {
        sortPrefs.edit { putString(entityType, sort) }
    }

    override fun getFilter(entityType: String): List<Any>? {
        val json = getRawFilter(entityType)
        return if (json.isNotEmpty()) {
            moshi
                .adapter<List<Any>>(Types.newParameterizedType(List::class.java, Any::class.java))
                .fromJson(json)
        } else {
            null
        }
    }

    override fun getRawFilter(entityType: String): String {
        return filterPrefs.getString(entityType, "").orEmpty()
    }

    override fun getParams(entityType: String): Map<String, Any>? {
        val json = getRawParams(entityType)
        return if (json.isNotEmpty()) {
            moshi
                .adapter<Map<String, Any>>(
                    Types.newParameterizedType(
                        Map::class.java,
                        String::class.java,
                        Any::class.java
                    )
                )
                .fromJson(json)
        } else {
            null
        }
    }

    override fun getRawParams(entityType: String): String {
        return paramsPrefs.getString(entityType, "").orEmpty()
    }

    override fun getSort(entityType: String): List<Any>? {
        val json = getRawSort(entityType)
        return if (json.isNotEmpty()) {
            moshi
                .adapter<List<Any>>(Types.newParameterizedType(List::class.java, Any::class.java))
                .fromJson(json)
        } else {
            null
        }
    }

    override fun getRawSort(entityType: String): String {
        return sortPrefs.getString(entityType, "").orEmpty()
    }
}
