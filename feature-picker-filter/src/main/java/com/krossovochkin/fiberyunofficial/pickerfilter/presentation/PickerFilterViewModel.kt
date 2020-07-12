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

package com.krossovochkin.fiberyunofficial.pickerfilter.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiConstants
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiRepository
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.Event
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.ResProvider
import by.krossovochkin.fiberyunofficial.core.presentation.ToolbarViewState
import com.krossovochkin.fiberyunofficial.pickerfilter.R
import com.krossovochkin.fiberyunofficial.pickerfilter.domain.FilterCondition
import com.krossovochkin.fiberyunofficial.pickerfilter.domain.FilterItemData
import com.krossovochkin.fiberyunofficial.pickerfilter.domain.SingleSelectFilterItemData
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.launch

class PickerFilterViewModel(
    private val pickerFilterArgs: PickerFilterFragment.Args,
    private val fiberyApiRepository: FiberyApiRepository,
    private val resProvider: ResProvider
) : ViewModel() {

    private val mutableItems = MutableLiveData<List<ListItem>>()
    val items: LiveData<List<ListItem>> = mutableItems

    private val mutableNavigation = MutableLiveData<Event<PickerFilterNavEvent>>()
    val navigation: LiveData<Event<PickerFilterNavEvent>> = mutableNavigation

    private val data: MutableList<FilterItemData> = mutableListOf()

    private var supportedFields = listOf<FiberyFieldSchema>()

    val toolbarViewState: ToolbarViewState
        get() = ToolbarViewState(
            title = resProvider.getString(R.string.pickerFilter_toolbarTitle),
            bgColorInt = ColorUtils.getColor(pickerFilterArgs.entityTypeSchema.meta.uiColorHex),
            hasBackButton = true
        )

    init {
        viewModelScope.launch {
            supportedFields = pickerFilterArgs.entityTypeSchema.fields
                .filter {
                    fiberyApiRepository.getTypeSchema(it.type).meta.isEnum
                }

            (pickerFilterArgs.filter to pickerFilterArgs.params).fromJson()
                ?.let { data.add(it) }

            update()
        }
    }

    fun onFieldSelected(position: Int, field: FiberyFieldSchema) {
        viewModelScope.launch {
            if (position >= data.size) {
                data.add(
                    SingleSelectFilterItemData(
                        field = field,
                        condition = null,
                        items = fiberyApiRepository.getEnumValues(field.type),
                        selectedItem = null
                    )
                )
            } else {
                data[position] = SingleSelectFilterItemData(
                    field = field,
                    condition = null,
                    items = fiberyApiRepository.getEnumValues(field.type),
                    selectedItem = null
                )
            }

            update()
        }
    }

    fun onConditionSelected(position: Int, condition: FilterCondition) {
        val item = data[position]
        data[position] = if (item is SingleSelectFilterItemData) {
            item.copy(condition = condition)
        } else {
            TODO("implement")
        }
        update()
    }

    fun onSingleSelectValueSelected(position: Int, value: FieldData.EnumItemData) {
        val item = data[position] as SingleSelectFilterItemData
        data[position] = item.copy(selectedItem = value)
        update()
    }

    fun applyFilter() {
        val item = data.first() as SingleSelectFilterItemData
        val (filter, params) = item.toJson()

        mutableNavigation.postValue(
            Event(
                PickerFilterNavEvent.ApplyFilterEvent(
                    filter = filter,
                    params = params
                )
            )
        )
    }

    fun onBackPressed() {
        mutableNavigation.postValue(Event(PickerFilterNavEvent.BackEvent))
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun Pair<String, String>.fromJson(): FilterItemData? {
        return runCatching {
            val (filter, params) = this
            val moshi = Moshi.Builder().build()

            val filterData: List<Any> = moshi
                .adapter<List<Any>>(Types.newParameterizedType(List::class.java, Any::class.java))
                .fromJson(filter)
                .orEmpty()
            val paramsData: Map<String, Any> = moshi
                .adapter<Map<String, Any>>(
                    Types.newParameterizedType(
                        Map::class.java,
                        String::class.java,
                        Any::class.java
                    )
                )
                .fromJson(params)
                .orEmpty()

            val fieldName = (filterData[1] as List<Any>)[0] as String
            val field = supportedFields.find { it.name == fieldName }!!
            val condition = FilterCondition.values().find { it.value == filterData[0] as String }!!
            val items = fiberyApiRepository.getEnumValues(field.type)
            val selectedItem = items.find { it.id == paramsData["\$where1"] }

            SingleSelectFilterItemData(
                field = field,
                condition = condition,
                items = items,
                selectedItem = selectedItem
            )
        }.getOrNull()
    }

    private fun FilterItemData.toJson(): Pair<String, String> {
        return if (this is SingleSelectFilterItemData) {
            val itemCondition = this.condition!!.value
            val itemFieldName = this.field.name
            val itemId = this.selectedItem!!.id

            val filter =
                "[\"$itemCondition\",[\"$itemFieldName\", \"${FiberyApiConstants.Field.ID.value}\"],\"\$where1\"]"
            val params = "{\"\$where1\": \"$itemId\"}"

            filter to params
        } else {
            TODO("implement")
        }
    }

    private fun update() {
        mutableItems.postValue(map(data))
    }

    private fun map(data: List<FilterItemData>): List<ListItem> {
        return if (data.isEmpty()) {
            listOf(
                EmptyFilterItem(
                    fields = supportedFields
                )
            )
        } else {
            data.map(::map)
        }
    }

    private fun map(data: FilterItemData): ListItem {
        return if (data is SingleSelectFilterItemData) {
            SingleSelectFilterItem(
                fields = supportedFields,
                field = data.field,
                conditions = FilterCondition.values().toList(),
                condition = data.condition,
                values = data.items,
                selectedValue = data.selectedItem
            )
        } else {
            TODO("implement")
        }
    }
}
