package com.krossovochkin.fiberyunofficial.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.krossovochkin.fiberyunofficial.domain.FiberyAppData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityFilterData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntitySortData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.FieldData
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val loginInteractor: com.krossovochkin.fiberyunofficial.login.domain.LoginInteractor,
    private val resultBus: com.krossovochkin.core.presentation.result.ResultBus
) : ViewModel() {

    private val _backstack = MutableStateFlow<List<NavKey>>(listOf(LoginNavKey))
    val backstack: StateFlow<List<NavKey>> = _backstack.asStateFlow()

    init {
        viewModelScope.launch {
            if (loginInteractor.isLoggedIn()) {
                onLoginSuccess()
            }
        }
    }

    fun onAppSelected(fiberyAppData: FiberyAppData) {
        _backstack.update { it + EntityTypeListNavKey(fiberyAppData) }
    }

    fun onEntityTypeSelected(entityTypeSchema: FiberyEntityTypeSchema) {
        _backstack.update {
            it + EntityListNavKey(
                entityType = entityTypeSchema,
                parentEntityData = null
            )
        }
    }

    fun onEntityTypeSelected(
        entityTypeSchema: FiberyEntityTypeSchema,
        parentEntityData: ParentEntityData
    ) {
        val key = when (entityTypeSchema.name) {
            "fibery/file" -> FileListNavKey(entityTypeSchema, parentEntityData)
            "fibery/comment" -> CommentListNavKey(entityTypeSchema, parentEntityData)
            else -> EntityListNavKey(entityTypeSchema, parentEntityData)
        }
        _backstack.update { it + key }
    }

    fun onEntitySelected(entity: FiberyEntityData) {
        _backstack.update { it + EntityDetailsNavKey(entity) }
    }

    fun onLoginSuccess() {
        _backstack.value = listOf(AppListNavKey)
    }

    fun onAddEntityRequested(
        entityType: FiberyEntityTypeSchema,
        parentEntityData: ParentEntityData?
    ) {
        val key = if (parentEntityData == null) {
            EntityCreateNavKey(entityType)
        } else {
            EntityPickerNavKey(parentEntityData, null)
        }
        _backstack.update { it + key }
    }

    fun onEntityCreateSuccess() {
        pop()
    }

    fun onEntityFieldEdit(
        parentEntityData: ParentEntityData,
        entity: FiberyEntityData?
    ) {
        _backstack.update { it + EntityPickerNavKey(parentEntityData, entity) }
    }

    fun onEntityPicked(
        parentEntityData: ParentEntityData,
        entity: FiberyEntityData?
    ) {
        viewModelScope.launch {
            resultBus.sendResult(
                com.krossovochkin.fiberyunofficial.domain.PickerEntityResultData(
                    fieldSchema = parentEntityData.fieldSchema,
                    entity = entity
                )
            )
        }
        pop()
    }

    fun onSingleSelectFieldEdit(
        parentEntityData: ParentEntityData,
        item: FieldData.SingleSelectFieldData
    ) {
        _backstack.update { it + PickerSingleSelectNavKey(item, parentEntityData) }
    }

    fun onSingleSelectPicked(
        parentEntityData: ParentEntityData,
        selectedValue: FieldData.EnumItemData?
    ) {
        viewModelScope.launch {
            resultBus.sendResult(
                com.krossovochkin.fiberyunofficial.domain.PickerSingleSelectResultData(
                    fieldSchema = parentEntityData.fieldSchema,
                    selectedValue = selectedValue
                )
            )
        }
        pop()
    }

    fun onMultiSelectFieldEdit(
        parentEntityData: ParentEntityData,
        item: FieldData.MultiSelectFieldData
    ) {
        _backstack.update { it + PickerMultiSelectNavKey(item, parentEntityData) }
    }

    fun onMultiSelectPicked(
        parentEntityData: ParentEntityData,
        addedItems: List<FieldData.EnumItemData>,
        removedItems: List<FieldData.EnumItemData>
    ) {
        viewModelScope.launch {
            resultBus.sendResult(
                com.krossovochkin.fiberyunofficial.domain.MultiSelectPickedData(
                    fieldSchema = parentEntityData.fieldSchema,
                    addedItems = addedItems,
                    removedItems = removedItems
                )
            )
        }
        pop()
    }

    fun onFilterEdit(
        entityTypeSchema: FiberyEntityTypeSchema,
        filter: FiberyEntityFilterData?
    ) {
        _backstack.update { it + PickerFilterNavKey(entityTypeSchema, filter) }
    }

    fun onFilterSelected(
        entityType: FiberyEntityTypeSchema,
        filter: FiberyEntityFilterData
    ) {
        viewModelScope.launch {
            resultBus.sendResult(
                com.krossovochkin.fiberyunofficial.domain.PickerFilterResultData(
                    entityType = entityType,
                    filter = filter
                )
            )
        }
        pop()
    }

    fun onSortEdit(
        entityTypeSchema: FiberyEntityTypeSchema,
        sort: FiberyEntitySortData?
    ) {
        _backstack.update { it + PickerSortNavKey(entityTypeSchema, sort) }
    }

    fun onSortSelected(
        entityType: FiberyEntityTypeSchema,
        sort: FiberyEntitySortData
    ) {
        viewModelScope.launch {
            resultBus.sendResult(
                com.krossovochkin.fiberyunofficial.domain.PickerSortResultData(
                    entityType = entityType,
                    sort = sort
                )
            )
        }
        pop()
    }

    fun pop() {
        _backstack.update {
            if (it.size > 1) it.dropLast(1) else it
        }
    }
}
