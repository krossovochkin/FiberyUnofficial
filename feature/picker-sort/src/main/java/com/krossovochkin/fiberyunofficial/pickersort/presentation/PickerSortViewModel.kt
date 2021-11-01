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

package com.krossovochkin.fiberyunofficial.pickersort.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krossovochkin.core.presentation.list.ListItem
import com.krossovochkin.core.presentation.resources.NativeColor
import com.krossovochkin.core.presentation.resources.NativeText
import com.krossovochkin.core.presentation.ui.toolbar.ToolbarViewState
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.pickersort.R
import com.krossovochkin.fiberyunofficial.pickersort.domain.EmptySortItemData
import com.krossovochkin.fiberyunofficial.pickersort.domain.SelectedSortItemData
import com.krossovochkin.fiberyunofficial.pickersort.domain.SortCondition
import com.krossovochkin.fiberyunofficial.pickersort.domain.SortItemData
import com.krossovochkin.serialization.Serializer
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class PickerSortViewModel : ViewModel() {

    abstract val navigation: Flow<PickerSortNavEvent>

    abstract val items: Flow<List<ListItem>>

    abstract val toolbarViewState: ToolbarViewState

    abstract fun onFieldSelected(position: Int, field: FiberyFieldSchema?)

    abstract fun onConditionSelected(position: Int, condition: SortCondition?)

    abstract fun onAddSortClicked()

    abstract fun applySort()

    abstract fun onBackPressed()
}

class PickerSortViewModelImpl(
    private val pickerSortArgs: PickerSortFragment.Args,
    private val serializer: Serializer
) : PickerSortViewModel() {

    override val items = MutableStateFlow<List<ListItem>>(emptyList())

    private val navigationChannel = Channel<PickerSortNavEvent>(Channel.BUFFERED)
    override val navigation: Flow<PickerSortNavEvent>
        get() = navigationChannel.receiveAsFlow()

    private val data: MutableList<SortItemData> = mutableListOf()

    private var supportedFields = listOf<FiberyFieldSchema>()

    override val toolbarViewState: ToolbarViewState
        get() = ToolbarViewState(
            title = NativeText.Resource(R.string.picker_sort_toolbar_title),
            bgColor = NativeColor.Hex(pickerSortArgs.entityTypeSchema.meta.uiColorHex),
            hasBackButton = true
        )

    init {
        viewModelScope.launch {
            supportedFields = pickerSortArgs.entityTypeSchema.fields

            pickerSortArgs.sort.fromJson()
                .let { items ->
                    data.clear()
                    data.addAll(items)
                }

            if (data.isEmpty()) {
                data.add(EmptySortItemData)
            }

            update()
        }
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
                    SelectedSortItemData(
                        field = field,
                        condition = null,
                    )
                )
            } else {
                data[position] = SelectedSortItemData(
                    field = field,
                    condition = null,
                )
            }

            update()
        }
    }

    override fun onConditionSelected(position: Int, condition: SortCondition?) {
        val item = data[position]
        data[position] = if (item is SelectedSortItemData) {
            item.copy(condition = condition)
        } else {
            TODO("implement")
        }
        update()
    }

    override fun onAddSortClicked() {
        data.add(EmptySortItemData)
        update()
    }

    override fun applySort() {
        val sort = if (data.isEmpty()) {
            ""
        } else {
            data.toJson()
        }

        viewModelScope.launch {
            navigationChannel.send(
                PickerSortNavEvent.ApplySortEvent(
                    sort = sort
                )
            )
        }
    }

    override fun onBackPressed() {
        viewModelScope.launch {
            navigationChannel.send(
                PickerSortNavEvent.BackEvent
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun String.fromJson(): List<SortItemData> {
        return runCatching {
            val sort = this
            val sortData: List<List<List<Any>>> =
                serializer.jsonToList(sort, Any::class.java) as List<List<List<Any>>>

            sortData.map { sortItem: List<Any> ->
                val fieldName = (sortItem.first() as List<Any>).first() as String
                val conditionName = sortItem[1] as String
                SelectedSortItemData(
                    field = supportedFields.find { it.name == fieldName }!!,
                    condition = SortCondition.values().find { it.value == conditionName }!!
                )
            }
        }.getOrNull() ?: emptyList()
    }

    private fun List<SortItemData>.toJson(): String {
        if (this.isEmpty()) {
            return ""
        }

        val itemsJsons = this.map { data -> data.toJson() }

        return "[${itemsJsons.joinToString()}]"
    }

    private fun SortItemData.toJson(): String {
        return if (this is SelectedSortItemData) {
            val itemCondition = this.condition!!.value
            val itemFieldName = this.field.name

            "[[\"$itemFieldName\"], \"$itemCondition\"]"
        } else {
            TODO("implement")
        }
    }

    private fun update() {
        viewModelScope.launch {
            items.emit(
                data.map(::map) +
                    AddSortItem
            )
        }
    }

    private fun map(data: SortItemData): ListItem {
        return when {
            data is SelectedSortItemData -> {
                SelectedSortItem(
                    fields = supportedFields,
                    field = data.field,
                    conditions = SortCondition.values().toList(),
                    condition = data.condition,
                )
            }
            data === EmptySortItemData -> {
                EmptySortItem(
                    fields = supportedFields
                )
            }
            else -> {
                TODO("implement")
            }
        }
    }
}
