/*
   Copyright 2020 Vasya Drobushkov

   Licensed under the Apache License, Version 2.0 (the \"License\");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an \"AS IS\" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package com.krossovochkin.fiberyunofficial.pickersort.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krossovochkin.core.presentation.resources.NativeColor
import com.krossovochkin.core.presentation.resources.NativeText
import com.krossovochkin.core.presentation.ui.toolbar.ToolbarViewState
import com.krossovochkin.fiberyunofficial.domain.FiberyEntitySortData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.navigation.PickerSortNavKey
import com.krossovochkin.fiberyunofficial.pickersort.R
import com.krossovochkin.fiberyunofficial.pickersort.domain.EmptySortItemData
import com.krossovochkin.fiberyunofficial.pickersort.domain.SelectedSortItemData
import com.krossovochkin.fiberyunofficial.pickersort.domain.SortCondition
import com.krossovochkin.fiberyunofficial.pickersort.domain.SortItemData
import com.krossovochkin.fiberyunofficial.ui.list.ListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@HiltViewModel(assistedFactory = PickerSortViewModel.Factory::class)
class PickerSortViewModel @AssistedInject constructor(
    @Assisted private val pickerSortArgs: PickerSortNavKey,
) : ViewModel() {

    val items = MutableStateFlow<List<ListItem>>(emptyList())

    private val data: MutableList<SortItemData> = mutableListOf()

    private var supportedFields = listOf<FiberyFieldSchema>()

    val toolbarViewState: ToolbarViewState
        get() = ToolbarViewState(
            title = NativeText.Resource(R.string.picker_sort_toolbar_title),
            bgColor = NativeColor.Hex(pickerSortArgs.entityTypeSchema.meta.uiColorHex),
            hasBackButton = true
        )

    init {
        viewModelScope.launch {
            supportedFields = pickerSortArgs.entityTypeSchema.fields

            pickerSortArgs.sort?.items
                ?.let { items ->
                    data.clear()
                    data.addAll(
                        items.map { item ->
                            SelectedSortItemData(
                                field = item.field,
                                condition = SortCondition.fromCondition(item.condition)
                            )
                        }
                    )
                }

            if (data.isEmpty()) {
                data.add(EmptySortItemData)
            }

            update()
        }
    }

    fun onFieldSelected(position: Int, field: FiberyFieldSchema?) {
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

    fun onConditionSelected(position: Int, condition: SortCondition?) {
        val item = data[position]
        data[position] = if (item is SelectedSortItemData) {
            item.copy(condition = condition)
        } else {
            TODO("implement")
        }
        update()
    }

    fun onAddSortClicked() {
        data.add(EmptySortItemData)
        update()
    }

    fun applySort(onSortApply: (FiberyEntityTypeSchema, FiberyEntitySortData) -> Unit) {
        val sort = FiberyEntitySortData(
            items = data.mapNotNull { item ->
                if (item is SelectedSortItemData) {
                    val condition = item.condition?.value ?: return@mapNotNull null
                    FiberyEntitySortData.Item(
                        field = item.field,
                        condition = condition
                    )
                } else {
                    null
                }
            }
        )

        onSortApply(pickerSortArgs.entityTypeSchema, sort)
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

    @AssistedFactory
    interface Factory {
        fun create(
            args: PickerSortNavKey,
        ): PickerSortViewModel
    }
}
