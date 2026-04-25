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

package com.krossovochkin.fiberyunofficial.entitylist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import androidx.paging.PagingData
import com.krossovochkin.core.presentation.resources.NativeColor
import com.krossovochkin.core.presentation.resources.NativeText
import com.krossovochkin.core.presentation.ui.fab.FabViewState
import com.krossovochkin.core.presentation.ui.toolbar.ToolbarAction
import com.krossovochkin.core.presentation.ui.toolbar.ToolbarViewState
import com.krossovochkin.core.presentation.result.ResultBus
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityFilterData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntitySortData
import com.krossovochkin.fiberyunofficial.domain.PickerFilterResultData
import com.krossovochkin.fiberyunofficial.domain.PickerSortResultData
import com.krossovochkin.fiberyunofficial.entitylist.R
import com.krossovochkin.fiberyunofficial.entitylist.domain.AddEntityRelationInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListFilterInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListSortInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.RemoveEntityRelationInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListFilterInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListSortInteractor
import com.krossovochkin.fiberyunofficial.navigation.EntityListNavKey
import com.krossovochkin.fiberyunofficial.ui.list.ListItem
import com.krossovochkin.fiberyunofficial.ui.paging.PaginatedListViewModelDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@HiltViewModel(assistedFactory = EntityListViewModel.Factory::class)
class EntityListViewModel @AssistedInject constructor(
    getEntityListInteractor: GetEntityListInteractor,
    private val setEntityListFilterInteractor: SetEntityListFilterInteractor,
    private val setEntityListSortInteractor: SetEntityListSortInteractor,
    private val getEntityListFilterInteractor: GetEntityListFilterInteractor,
    private val getEntityListSortInteractor: GetEntityListSortInteractor,
    private val removeEntityRelationInteractor: RemoveEntityRelationInteractor,
    private val addEntityRelationInteractor: AddEntityRelationInteractor,
    private val resultBus: ResultBus,
    @Assisted private val entityListArgs: EntityListNavKey,
) : ViewModel() {

    val progress = MutableStateFlow(false)
    private val errorChannel = Channel<Exception>(Channel.BUFFERED)
    val error: Flow<Exception>
        get() = errorChannel.receiveAsFlow()

    private val paginatedListDelegate = PaginatedListViewModelDelegate(
        viewModel = this,
        loadPage = { offset: Int, pageSize: Int ->
            getEntityListInteractor
                .execute(
                    entityListArgs.entityType,
                    offset,
                    pageSize,
                    entityListArgs.parentEntityData
                )
        },
        mapper = { entity ->
            EntityListItem(
                title = entity.title,
                entityData = entity,
                isRemoveAvailable = entityListArgs.parentEntityData != null
            )
        }
    )

    val entityItems: Flow<PagingData<ListItem>>
        get() = paginatedListDelegate.items

    val toolbarViewState: ToolbarViewState
        get() = ToolbarViewState(
            title = NativeText.Simple(
                entityListArgs.parentEntityData?.fieldSchema?.displayName
                    ?: entityListArgs.entityType.displayName
            ),
            bgColor = NativeColor.Hex(entityListArgs.entityType.meta.uiColorHex),
            hasBackButton = true,
            actions = if (entityListArgs.parentEntityData == null) {
                listOf(ToolbarAction.FILTER, ToolbarAction.SORT)
            } else {
                emptyList()
            }
        )

    init {
        viewModelScope.launch {
            resultBus.results.collect { result ->
                when (result) {
                    is PickerFilterResultData -> {
                        if (result.entityType == entityListArgs.entityType) {
                            onFilterSelected(result.filter)
                        }
                    }
                    is PickerSortResultData -> {
                        if (result.entityType == entityListArgs.entityType) {
                            onSortSelected(result.sort)
                        }
                    }
                }
            }
        }
    }

    fun getCreateFabViewState() =
        FabViewState(
            bgColor = NativeColor.Attribute(androidx.appcompat.R.attr.colorPrimary)
        )

    fun removeRelation(item: EntityListItem) {
        val parentEntityData = entityListArgs.parentEntityData
            ?: error("Can't remove relation from top-level entity list")

        viewModelScope.launch {
            try {
                removeEntityRelationInteractor.execute(
                    parentEntityData = parentEntityData,
                    childEntity = item.entityData
                )
                paginatedListDelegate.invalidate()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorChannel.send(e)
            }
        }
    }

    fun onFilterSelected(filter: FiberyEntityFilterData) {
        viewModelScope.launch {
            setEntityListFilterInteractor.execute(entityListArgs.entityType, filter)
            paginatedListDelegate.invalidate()
        }
    }

    fun onSortSelected(sort: FiberyEntitySortData) {
        viewModelScope.launch {
            setEntityListSortInteractor.execute(entityListArgs.entityType, sort)
            paginatedListDelegate.invalidate()
        }
    }

    fun onEntityCreated(createdEntity: FiberyEntityData) {
        val parentEntityData = entityListArgs.parentEntityData
        if (parentEntityData == null) {
            paginatedListDelegate.invalidate()
            return
        }

        viewModelScope.launch {
            try {
                addEntityRelationInteractor
                    .execute(
                        parentEntityData = parentEntityData,
                        childEntity = createdEntity
                    )
                paginatedListDelegate.invalidate()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorChannel.send(e)
            }
        }
    }

    fun onError(error: Exception) {
        if (error is CancellationException) {
            return
        }
        viewModelScope.launch {
            this@EntityListViewModel.errorChannel.send(error)
        }
    }

    fun getFilter() = getEntityListFilterInteractor.execute(entityListArgs.entityType)
    fun getSort() = getEntityListSortInteractor.execute(entityListArgs.entityType)
    fun getEntityType() = entityListArgs.entityType
    fun getParentEntityData() = entityListArgs.parentEntityData

    @AssistedFactory
    interface Factory {
        fun create(
            args: EntityListNavKey,
        ): EntityListViewModel
    }
}
