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

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krossovochkin.core.presentation.list.ListItem
import com.krossovochkin.core.presentation.resources.NativeColor
import com.krossovochkin.core.presentation.resources.NativeText
import com.krossovochkin.core.presentation.ui.toolbar.ToolbarViewState
import com.krossovochkin.fiberyunofficial.api.FiberyApiConstants
import com.krossovochkin.fiberyunofficial.api.FiberyApiRepository
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.domain.FieldData
import com.krossovochkin.fiberyunofficial.pickerfilter.R
import com.krossovochkin.fiberyunofficial.pickerfilter.domain.EmptyFilterItemData
import com.krossovochkin.fiberyunofficial.pickerfilter.domain.FilterCondition
import com.krossovochkin.fiberyunofficial.pickerfilter.domain.FilterItemData
import com.krossovochkin.fiberyunofficial.pickerfilter.domain.FilterMergeType
import com.krossovochkin.fiberyunofficial.pickerfilter.domain.SingleSelectFilterItemData
import com.krossovochkin.serialization.Serializer
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class PickerFilterViewModel : ViewModel() {

    abstract val navigation: Flow<PickerFilterNavEvent>

    abstract val items: Flow<List<ListItem>>

    abstract val toolbarViewState: ToolbarViewState

    abstract fun onMergeTypeSelected(type: FilterMergeType)

    abstract fun onFieldSelected(position: Int, field: FiberyFieldSchema?)

    abstract fun onConditionSelected(position: Int, condition: FilterCondition?)

    abstract fun onSingleSelectValueSelected(position: Int, value: FieldData.EnumItemData?)

    abstract fun onAddFilterClicked()

    abstract fun applyFilter()

    abstract fun onBackPressed()
}

class PickerFilterViewModelImpl(
    private val pickerFilterArgs: PickerFilterFragment.Args,
    private val fiberyApiRepository: FiberyApiRepository,
    private val serializer: Serializer
) : PickerFilterViewModel() {

    override val items = MutableStateFlow<List<ListItem>>(emptyList())

    private val navigationChannel = Channel<PickerFilterNavEvent>(Channel.BUFFERED)
    override val navigation: Flow<PickerFilterNavEvent>
        get() = navigationChannel.receiveAsFlow()

    private var mergeType: FilterMergeType = FilterMergeType.ALL
    private val data: MutableList<FilterItemData> = mutableListOf()

    private var supportedFields = listOf<FiberyFieldSchema>()

    override val toolbarViewState: ToolbarViewState
        get() = ToolbarViewState(
            title = NativeText.Resource(R.string.picker_filter_toolbar_title),
            bgColor = NativeColor.Hex(pickerFilterArgs.entityTypeSchema.meta.uiColorHex),
            hasBackButton = true
        )

    init {
        viewModelScope.launch {
            supportedFields = pickerFilterArgs.entityTypeSchema.fields
                .filter {
                    fiberyApiRepository.getTypeSchema(it.type).meta.isEnum &&
                        !it.meta.isCollection
                }

            (pickerFilterArgs.filter to pickerFilterArgs.params).fromJson()
                .let { (filterMergeType, items) ->
                    mergeType = filterMergeType
                    data.clear()
                    data.addAll(items)
                }

            if (data.isEmpty()) {
                data.add(EmptyFilterItemData)
            }

            update()
        }
    }

    override fun onMergeTypeSelected(type: FilterMergeType) {
        mergeType = type

        update()
    }

    override fun onFieldSelected(position: Int, field: FiberyFieldSchema?) {
        if (field == null) {
            data.removeAt(position)
            update()
            return
        }

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

    override fun onConditionSelected(position: Int, condition: FilterCondition?) {
        val item = data[position]
        data[position] = if (item is SingleSelectFilterItemData) {
            item.copy(condition = condition)
        } else {
            TODO("implement")
        }
        update()
    }

    override fun onSingleSelectValueSelected(position: Int, value: FieldData.EnumItemData?) {
        val item = data[position] as SingleSelectFilterItemData
        data[position] = item.copy(selectedItem = value)
        update()
    }

    override fun onAddFilterClicked() {
        data.add(EmptyFilterItemData)
        update()
    }

    override fun applyFilter() {
        val (filter, params) = if (data.isEmpty()) {
            "" to ""
        } else {
            (mergeType to data).toJson()
        }

        Log.e("HELLO", "filter: $filter")
        Log.e("HELLO", "params: $params")

        viewModelScope.launch {
            navigationChannel.send(
                PickerFilterNavEvent.ApplyFilterEvent(
                    filter = filter,
                    params = params
                )
            )
        }
    }

    override fun onBackPressed() {
        viewModelScope.launch {
            navigationChannel.send(
                PickerFilterNavEvent.BackEvent
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun Pair<String, String>.fromJson(): Pair<FilterMergeType, List<FilterItemData>> {
        return runCatching {
            val (filter, params) = this
            val filterData: List<Any> = serializer.jsonToList(filter, Any::class.java)

            val paramsData: Map<String, String> = serializer
                .jsonToMap(params, String::class.java, String::class.java)

            val mergeType = if (filterData.isEmpty()) {
                FilterMergeType.ALL
            } else {
                FilterMergeType.values().find { it.value == filterData.first() }!!
            }

            val items = filterData.drop(1).mapIndexed { index: Int, filterItem: Any ->
                (serializer.listToJson(
                    filterItem as List<Any>,
                    Any::class.java
                ) to paramsData["\$where${index + 1}"]!!).fromItemJson()!!
            }

            mergeType to items
        }.getOrNull() ?: (mergeType to emptyList())
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun Pair<String, String>.fromItemJson(): FilterItemData? {
        return runCatching {
            val (filter, params) = this
            val filterData: List<Any> = serializer.jsonToList(filter, Any::class.java)

            val fieldName = (filterData[1] as List<Any>)[0] as String
            val field = supportedFields.find { it.name == fieldName }!!
            val condition = FilterCondition.values().find { it.value == filterData[0] as String }!!
            val items = fiberyApiRepository.getEnumValues(field.type)
            val selectedItem = items.find { it.id == params }

            SingleSelectFilterItemData(
                field = field,
                condition = condition,
                items = items,
                selectedItem = selectedItem
            )
        }.getOrNull()
    }

    private fun Pair<FilterMergeType, List<FilterItemData>>.toJson(): Pair<String, String> {
        val (mergeType, items) = this
        val itemsJsons = items.mapIndexed { index, data -> data.toJson(index + 1) }

        val filterJson = "[\"${mergeType.value}\", ${itemsJsons.joinToString { it.first }}]"
        val paramsJson = "{${itemsJsons.joinToString { it.second }}}"

        return filterJson to paramsJson
    }

    private fun FilterItemData.toJson(index: Int): Pair<String, String> {
        return if (this is SingleSelectFilterItemData) {
            val itemCondition = this.condition!!.value
            val itemFieldName = this.field.name
            val itemId = this.selectedItem!!.id

            val filter =
                "[\"$itemCondition\",[\"$itemFieldName\", \"${FiberyApiConstants.Field.ID.value}\"],\"\$where$index\"]"
            val params = "\"\$where$index\": \"$itemId\""

            filter to params
        } else {
            TODO("implement")
        }
    }

    private fun update() {
        viewModelScope.launch {
            items.emit(
                listOf(FilterMergeTypeItem(mergeType)) +
                    data.map(::map) +
                    AddFilterItem
            )
        }
    }

    private fun map(data: FilterItemData): ListItem {
        return when {
            data is SingleSelectFilterItemData -> {
                SingleSelectFilterItem(
                    fields = supportedFields,
                    field = data.field,
                    conditions = FilterCondition.values().toList(),
                    condition = data.condition,
                    values = data.items,
                    selectedValue = data.selectedItem
                )
            }
            data === EmptyFilterItemData -> {
                EmptyFilterItem(
                    fields = supportedFields
                )
            }
            else -> {
                TODO("implement")
            }
        }
    }
}
