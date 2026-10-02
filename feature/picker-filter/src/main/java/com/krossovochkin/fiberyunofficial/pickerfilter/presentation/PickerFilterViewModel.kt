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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krossovochkin.core.presentation.resources.NativeColor
import com.krossovochkin.core.presentation.resources.NativeText
import com.krossovochkin.core.presentation.ui.toolbar.ToolbarViewState
import com.krossovochkin.fiberyunofficial.api.FiberyApiRepository
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityFilterData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.domain.FieldData
import com.krossovochkin.fiberyunofficial.navigation.PickerFilterNavKey
import com.krossovochkin.fiberyunofficial.pickerfilter.R
import com.krossovochkin.fiberyunofficial.pickerfilter.domain.EmptyFilterItemData
import com.krossovochkin.fiberyunofficial.pickerfilter.domain.FilterCondition
import com.krossovochkin.fiberyunofficial.pickerfilter.domain.FilterItemData
import com.krossovochkin.fiberyunofficial.pickerfilter.domain.FilterMergeType
import com.krossovochkin.fiberyunofficial.pickerfilter.domain.SingleSelectFilterItemData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = PickerFilterViewModel.Factory::class)
class PickerFilterViewModel @AssistedInject constructor(
    private val fiberyApiRepository: FiberyApiRepository,
    @Assisted private val pickerFilterArgs: PickerFilterNavKey,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PickerFilterUiState())
    val uiState: StateFlow<PickerFilterUiState> = _uiState.asStateFlow()

    private data class RowEntry(
        val id: Long,
        var data: FilterItemData,
    )

    private val rows: MutableList<RowEntry> = mutableListOf()
    private var nextId: Long = 0L

    private var mergeType: FilterMergeType = FilterMergeType.ALL

    private var supportedFields = listOf<FiberyFieldSchema>()

    val toolbarViewState: ToolbarViewState
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

            pickerFilterArgs.filter?.let { filter ->
                mergeType = FilterMergeType.fromMergeType(filter.mergeType)
                rows.clear()
                rows.addAll(
                    filter.items.mapNotNull { item ->
                        if (item is FiberyEntityFilterData.Item.SingleSelectItem) {
                            RowEntry(
                                id = nextId++,
                                data = SingleSelectFilterItemData(
                                    field = item.field,
                                    condition = FilterCondition.fromCondition(item.condition),
                                    items = fiberyApiRepository.getEnumValues(item.field.type),
                                    selectedItem = item.param
                                )
                            )
                        } else {
                            null
                        }
                    }
                )
            }

            if (rows.isEmpty()) {
                rows.add(RowEntry(id = nextId++, data = EmptyFilterItemData))
            }

            refresh()
        }
    }

    fun onMergeTypeSelected(type: FilterMergeType) {
        if (mergeType == type) return
        mergeType = type
        refresh()
    }

    fun onFieldSelected(position: Int, field: FiberyFieldSchema?) {
        if (position !in rows.indices) return
        if (field == null) {
            rows.removeAt(position)
            if (rows.isEmpty()) {
                rows.add(RowEntry(id = nextId++, data = EmptyFilterItemData))
            }
            refresh()
            return
        }

        viewModelScope.launch {
            val values = fiberyApiRepository.getEnumValues(field.type)
            val entry = rows.getOrNull(position) ?: return@launch
            entry.data = SingleSelectFilterItemData(
                field = field,
                condition = null,
                items = values,
                selectedItem = null
            )
            refresh()
        }
    }

    fun onConditionSelected(position: Int, condition: FilterCondition?) {
        val entry = rows.getOrNull(position) ?: return
        val item = entry.data as? SingleSelectFilterItemData ?: return
        entry.data = item.copy(condition = condition)
        refresh()
    }

    fun onSingleSelectValueSelected(position: Int, value: FieldData.EnumItemData?) {
        val entry = rows.getOrNull(position) ?: return
        val item = entry.data as? SingleSelectFilterItemData ?: return
        entry.data = item.copy(selectedItem = value)
        refresh()
    }

    fun onDeleteClicked(position: Int) {
        if (position !in rows.indices) return
        rows.removeAt(position)
        if (rows.isEmpty()) {
            rows.add(RowEntry(id = nextId++, data = EmptyFilterItemData))
        }
        refresh()
    }

    fun onAddFilterClicked() {
        if (rows.any { it.data === EmptyFilterItemData }) return
        rows.add(RowEntry(id = nextId++, data = EmptyFilterItemData))
        refresh()
    }

    fun applyFilter(onFilterApply: (FiberyEntityTypeSchema, FiberyEntityFilterData) -> Unit) {
        if (!_uiState.value.isApplyEnabled) return
        val filter = FiberyEntityFilterData(
            mergeType = mergeType.value,
            items = rows.mapNotNull { entry ->
                val item = entry.data
                if (item is SingleSelectFilterItemData) {
                    val condition = item.condition?.value ?: return@mapNotNull null
                    val param = item.selectedItem ?: return@mapNotNull null
                    FiberyEntityFilterData.Item.SingleSelectItem(
                        field = item.field,
                        condition = condition,
                        param = param
                    )
                } else {
                    null
                }
            }
        )
        onFilterApply(pickerFilterArgs.entityTypeSchema, filter)
    }

    private fun refresh() {
        var order = 0
        val uiRows = rows.map { entry ->
            when (val data = entry.data) {
                is SingleSelectFilterItemData -> {
                    order++
                    SingleSelectFilterItem(
                        key = entry.id,
                        order = order,
                        fields = supportedFields,
                        field = data.field,
                        conditions = FilterCondition.entries.toList(),
                        condition = data.condition,
                        values = data.items,
                        selectedValue = data.selectedItem,
                        showConditionError = data.condition == null,
                        showValueError = data.condition != null && data.selectedItem == null,
                    )
                }
                else -> {
                    EmptyFilterItem(
                        key = entry.id,
                        fields = supportedFields
                    )
                }
            }
        }

        val selectedRows = uiRows.filterIsInstance<SingleSelectFilterItem>()
        val validCount = selectedRows.count { it.condition != null && it.selectedValue != null }
        val hasIncomplete = uiRows.any { row ->
            when (row) {
                is EmptyFilterItem -> true
                is SingleSelectFilterItem -> row.condition == null || row.selectedValue == null
                else -> false
            }
        }

        _uiState.value = PickerFilterUiState(
            mergeType = mergeType,
            rows = listOf(FilterMergeTypeItem(mergeType)) + uiRows + AddFilterItem,
            validFilterCount = validCount,
            hasIncompleteRows = hasIncomplete,
            isApplyEnabled = validCount > 0 && !hasIncomplete,
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(
            args: PickerFilterNavKey,
        ): PickerFilterViewModel
    }
}
