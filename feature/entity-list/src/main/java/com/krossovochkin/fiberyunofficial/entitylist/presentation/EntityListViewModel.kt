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

package com.krossovochkin.fiberyunofficial.entitylist.presentation

import android.view.View
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.krossovochkin.core.presentation.list.ListItem
import com.krossovochkin.core.presentation.paging.PaginatedListViewModelDelegate
import com.krossovochkin.core.presentation.resources.NativeColor
import com.krossovochkin.core.presentation.resources.NativeText
import com.krossovochkin.core.presentation.ui.fab.FabViewState
import com.krossovochkin.core.presentation.ui.toolbar.ToolbarViewState
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityFilterData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntitySortData
import com.krossovochkin.fiberyunofficial.entitylist.R
import com.krossovochkin.fiberyunofficial.entitylist.domain.AddEntityRelationInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListFilterInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListSortInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.RemoveEntityRelationInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListFilterInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListSortInteractor
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntityListViewModel @Inject constructor(
    getEntityListInteractor: GetEntityListInteractor,
    private val setEntityListFilterInteractor: SetEntityListFilterInteractor,
    private val setEntityListSortInteractor: SetEntityListSortInteractor,
    private val getEntityListFilterInteractor: GetEntityListFilterInteractor,
    private val getEntityListSortInteractor: GetEntityListSortInteractor,
    private val removeEntityRelationInteractor: RemoveEntityRelationInteractor,
    private val addEntityRelationInteractor: AddEntityRelationInteractor,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val entityListArgs: EntityListFragmentArgs
        get() = EntityListFragmentArgs.fromSavedStateHandle(savedStateHandle)

    val progress = MutableStateFlow(false)
    private val errorChannel = Channel<Exception>(Channel.BUFFERED)
    val error: Flow<Exception>
        get() = errorChannel.receiveAsFlow()
    private val navigationChannel = Channel<EntityListNavEvent>(Channel.BUFFERED)
    val navigation: Flow<EntityListNavEvent>
        get() = navigationChannel.receiveAsFlow()

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
            menuResId = if (entityListArgs.parentEntityData == null) {
                R.menu.entity_list_menu
            } else {
                null
            }
        )

    fun getCreateFabViewState() =
        FabViewState(
            bgColor = NativeColor.Attribute(androidx.appcompat.R.attr.colorPrimary)
        )

    fun select(item: ListItem, itemView: View) {
        require(item is EntityListItem)
        viewModelScope.launch {
            navigationChannel.send(
                EntityListNavEvent.OnEntitySelectedEvent(item.entityData, itemView)
            )
        }
    }

    fun removeRelation(item: EntityListItem) {
        if (entityListArgs.parentEntityData == null) {
            error("Can't remove relation from top-level entity list")
        }

        viewModelScope.launch {
            try {
                removeEntityRelationInteractor.execute(
                    parentEntityData = entityListArgs.parentEntityData,
                    childEntity = item.entityData
                )
                paginatedListDelegate.invalidate()
            } catch (e: Exception) {
                errorChannel.send(e)
            }
        }
    }

    fun onBackPressed() {
        viewModelScope.launch {
            navigationChannel.send(EntityListNavEvent.BackEvent)
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

    fun onFilterClicked(view: View) {
        viewModelScope.launch {
            navigationChannel.send(
                EntityListNavEvent.OnFilterSelectedEvent(
                    entityTypeSchema = entityListArgs.entityType,
                    filter = getEntityListFilterInteractor.execute(entityListArgs.entityType),
                    view = view
                )
            )
        }
    }

    fun onSortClicked(view: View) {
        viewModelScope.launch {
            navigationChannel.send(
                EntityListNavEvent.OnSortSelectedEvent(
                    entityTypeSchema = entityListArgs.entityType,
                    sort = getEntityListSortInteractor.execute(entityListArgs.entityType),
                    view = view
                )
            )
        }
    }

    fun onCreateEntityClicked(view: View) {
        viewModelScope.launch {
            navigationChannel.send(
                EntityListNavEvent.OnCreateEntityEvent(
                    entityListArgs.entityType,
                    entityListArgs.parentEntityData,
                    view
                )
            )
        }
    }

    fun onEntityCreated(createdEntity: FiberyEntityData) {
        if (entityListArgs.parentEntityData == null) {
            paginatedListDelegate.invalidate()
            return
        }

        viewModelScope.launch {
            try {
                addEntityRelationInteractor
                    .execute(
                        parentEntityData = entityListArgs.parentEntityData,
                        childEntity = createdEntity
                    )
                paginatedListDelegate.invalidate()
            } catch (e: Exception) {
                errorChannel.send(e)
            }
        }
    }

    fun onError(error: Exception) {
        viewModelScope.launch {
            this@EntityListViewModel.errorChannel.send(error)
        }
    }
}
