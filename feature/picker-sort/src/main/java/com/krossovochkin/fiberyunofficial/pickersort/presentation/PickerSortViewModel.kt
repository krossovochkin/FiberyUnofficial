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
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = PickerSortViewModel.Factory::class)
class PickerSortViewModel @AssistedInject constructor(
    @Assisted private val pickerSortArgs: PickerSortNavKey,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PickerSortUiState())
    val uiState: StateFlow<PickerSortUiState> = _uiState.asStateFlow()

    private data class RowEntry(
        val id: Long,
        var data: SortItemData,
    )

    private val rows: MutableList<RowEntry> = mutableListOf()
    private var nextId: Long = 0L

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

            rows.clear()
            pickerSortArgs.sort?.items
                ?.forEach { item ->
                    rows.add(
                        RowEntry(
                            id = nextId++,
                            data = SelectedSortItemData(
                                field = item.field,
                                condition = SortCondition.fromCondition(item.condition)
                            )
                        )
                    )
                }

            if (rows.isEmpty()) {
                rows.add(RowEntry(id = nextId++, data = EmptySortItemData))
            }

            refresh()
        }
    }

    fun onFieldSelected(position: Int, field: FiberyFieldSchema?) {
        if (position !in rows.indices) return
        if (field == null) {
            rows.removeAt(position)
            if (rows.isEmpty()) {
                rows.add(RowEntry(id = nextId++, data = EmptySortItemData))
            }
            refresh()
            return
        }

        viewModelScope.launch {
            val entry = rows.getOrNull(position) ?: return@launch
            entry.data = SelectedSortItemData(
                field = field,
                condition = null,
            )
            refresh()
        }
    }

    fun onConditionSelected(position: Int, condition: SortCondition?) {
        val entry = rows.getOrNull(position) ?: return
        val item = entry.data
        entry.data = if (item is SelectedSortItemData) {
            item.copy(condition = condition)
        } else {
            return
        }
        refresh()
    }

    fun onDeleteClicked(position: Int) {
        if (position !in rows.indices) return
        rows.removeAt(position)
        if (rows.isEmpty()) {
            rows.add(RowEntry(id = nextId++, data = EmptySortItemData))
        }
        refresh()
    }

    fun onAddSortClicked() {
        if (rows.any { it.data === EmptySortItemData }) return
        rows.add(RowEntry(id = nextId++, data = EmptySortItemData))
        refresh()
    }

    fun applySort(onSortApply: (FiberyEntityTypeSchema, FiberyEntitySortData) -> Unit) {
        if (!_uiState.value.isApplyEnabled) return
        val sort = FiberyEntitySortData(
            items = rows.mapNotNull { entry ->
                val item = entry.data
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

    private fun refresh() {
        val fieldUsage = rows.mapNotNull { entry ->
            (entry.data as? SelectedSortItemData)?.field?.name
        }.groupingBy { it }.eachCount()

        var order = 0
        val uiRows = rows.map { entry ->
            when (val data = entry.data) {
                is SelectedSortItemData -> {
                    order++
                    SelectedSortItem(
                        key = entry.id,
                        order = order,
                        fields = supportedFields,
                        field = data.field,
                        conditions = SortCondition.entries.toList(),
                        condition = data.condition,
                        isDuplicateField = (fieldUsage[data.field.name] ?: 0) > 1,
                        showConditionError = data.condition == null,
                    )
                }
                else -> {
                    EmptySortItem(
                        key = entry.id,
                        fields = supportedFields
                    )
                }
            }
        }

        val selectedRows = uiRows.filterIsInstance<SelectedSortItem>()
        val validCount = selectedRows.count { it.condition != null && !it.isDuplicateField }
        val hasIncomplete = uiRows.any { row ->
            when (row) {
                is EmptySortItem -> true
                is SelectedSortItem -> row.condition == null || row.isDuplicateField
                else -> false
            }
        }

        _uiState.value = PickerSortUiState(
            rows = uiRows + AddSortItem,
            validSortCount = validCount,
            hasIncompleteRows = hasIncomplete,
            isApplyEnabled = validCount > 0 && !hasIncomplete,
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(
            args: PickerSortNavKey,
        ): PickerSortViewModel
    }
}
