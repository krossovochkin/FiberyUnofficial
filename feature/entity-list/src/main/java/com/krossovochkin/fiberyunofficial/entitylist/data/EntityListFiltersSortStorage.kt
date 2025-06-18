/*
   Copyright 2020 Vasya Drobushkov

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package com.krossovochkin.fiberyunofficial.entitylist.data

import android.content.Context
import androidx.core.content.edit
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityFilterData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntitySortData
import com.krossovochkin.serialization.Serializer
import javax.inject.Inject

private const val KEY_PREFS_FILTERS = "ENTITY_LIST_FILTERS"
private const val KEY_PREFS_SORT = "ENTITY_LIST_SORT"

class EntityListFiltersSortStorage @Inject constructor(
    context: Context,
    private val serializer: Serializer
) {

    private val filterPrefs = context.getSharedPreferences(KEY_PREFS_FILTERS, Context.MODE_PRIVATE)
    private val sortPrefs = context.getSharedPreferences(KEY_PREFS_SORT, Context.MODE_PRIVATE)
    private val filterPolymorphicData = listOf(
        Serializer.PolymorphicData(
            FiberyEntityFilterData.Item::class.java,
            "type",
            mapOf(
                FiberyEntityFilterData.Item.SingleSelectItem::class.java to
                    FiberyEntityFilterData.Item.Type.SINGLE_SELECT.name
            )
        )
    )

    fun setFilter(entityType: String, filter: FiberyEntityFilterData) {
        filterPrefs.edit {
            putString(
                entityType,
                serializer.polymorphicObjToJson(
                    filter,
                    FiberyEntityFilterData::class.java,
                    filterPolymorphicData
                )
            )
        }
    }

    fun setSort(entityType: String, sort: FiberyEntitySortData) {
        sortPrefs.edit {
            putString(
                entityType,
                serializer.objToJson(sort, FiberyEntitySortData::class.java)
            )
        }
    }

    fun getFilter(entityType: String): FiberyEntityFilterData? {
        val json = filterPrefs.getString(entityType, "").orEmpty()
        return if (json.isNotEmpty()) {
            serializer.jsonToPolymorphicObj(
                json,
                FiberyEntityFilterData::class.java,
                filterPolymorphicData
            )
        } else {
            null
        }
    }

    fun getSort(entityType: String): FiberyEntitySortData? {
        val json = sortPrefs.getString(entityType, "").orEmpty()
        return if (json.isNotEmpty()) {
            serializer.jsonToObj(json, FiberyEntitySortData::class.java)
        } else {
            null
        }
    }
}
