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

package by.krossovochkin.fiberyunofficial.entitylist.presentation

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.FabViewState
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.ResProvider
import by.krossovochkin.fiberyunofficial.core.presentation.ToolbarViewState
import by.krossovochkin.fiberyunofficial.core.presentation.common.PaginatedListViewModelDelegate
import by.krossovochkin.fiberyunofficial.entitylist.R
import by.krossovochkin.fiberyunofficial.entitylist.domain.AddEntityRelationInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListFilterInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListSortInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.RemoveEntityRelationInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListFilterInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListSortInteractor
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class EntityListViewModel : ViewModel() {

    abstract val progress: Flow<Boolean>

    abstract val error: Flow<Exception>

    abstract val navigation: Flow<EntityListNavEvent>

    abstract val entityItems: Flow<PagingData<ListItem>>

    abstract val toolbarViewState: ToolbarViewState

    abstract fun getCreateFabViewState(context: Context): FabViewState

    abstract fun select(item: ListItem, itemView: View)

    abstract fun removeRelation(item: EntityListItem)

    abstract fun onBackPressed()

    abstract fun onFilterClicked(view: View)

    abstract fun onFilterSelected(filter: String, params: String)

    abstract fun onSortClicked()

    abstract fun onSortSelected(sort: String)

    abstract fun onCreateEntityClicked(view: View)

    abstract fun onEntityCreated(createdEntity: FiberyEntityData)

    abstract fun onError(error: Exception)
}

internal class EntityListViewModelImpl(
    getEntityListInteractor: GetEntityListInteractor,
    private val setEntityListFilterInteractor: SetEntityListFilterInteractor,
    private val setEntityListSortInteractor: SetEntityListSortInteractor,
    private val getEntityListFilterInteractor: GetEntityListFilterInteractor,
    private val getEntityListSortInteractor: GetEntityListSortInteractor,
    private val removeEntityRelationInteractor: RemoveEntityRelationInteractor,
    private val addEntityRelationInteractor: AddEntityRelationInteractor,
    private val resProvider: ResProvider,
    private val entityListArgs: EntityListFragment.Args
) : EntityListViewModel() {

    override val progress = MutableStateFlow(false)
    private val errorChannel = Channel<Exception>(Channel.BUFFERED)
    override val error: Flow<Exception>
        get() = errorChannel.receiveAsFlow()
    private val navigationChannel = Channel<EntityListNavEvent>(Channel.BUFFERED)
    override val navigation: Flow<EntityListNavEvent>
        get() = navigationChannel.receiveAsFlow()

    private val paginatedListDelegate = PaginatedListViewModelDelegate(
        viewModel = this,
        loadPage = { offset: Int, pageSize: Int ->
            getEntityListInteractor
                .execute(
                    entityListArgs.entityTypeSchema,
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

    override val entityItems: Flow<PagingData<ListItem>>
        get() = paginatedListDelegate.items

    override val toolbarViewState: ToolbarViewState
        get() = ToolbarViewState(
            title = entityListArgs.parentEntityData?.fieldSchema?.displayName
                ?: entityListArgs.entityTypeSchema.displayName,
            bgColorInt = ColorUtils.getColor(entityListArgs.entityTypeSchema.meta.uiColorHex),
            hasBackButton = true,
            menuResId = if (entityListArgs.parentEntityData == null) {
                R.menu.entity_list_menu
            } else {
                null
            }
        )

    override fun getCreateFabViewState(context: Context) = FabViewState(
        bgColorInt = resProvider.getColorAttr(context, R.attr.colorPrimary)
    )

    override fun select(item: ListItem, itemView: View) {
        if (item is EntityListItem) {
            viewModelScope.launch {
                navigationChannel.send(
                    EntityListNavEvent.OnEntitySelectedEvent(item.entityData, itemView)
                )
            }
        } else {
            throw IllegalArgumentException()
        }
    }

    override fun removeRelation(item: EntityListItem) {
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

    override fun onBackPressed() {
        viewModelScope.launch {
            navigationChannel.send(EntityListNavEvent.BackEvent)
        }
    }

    override fun onFilterSelected(filter: String, params: String) {
        viewModelScope.launch {
            setEntityListFilterInteractor.execute(entityListArgs.entityTypeSchema, filter, params)
            paginatedListDelegate.invalidate()
        }
    }

    override fun onSortSelected(sort: String) {
        viewModelScope.launch {
            setEntityListSortInteractor.execute(entityListArgs.entityTypeSchema, sort)
            paginatedListDelegate.invalidate()
        }
    }

    override fun onFilterClicked(view: View) {
        viewModelScope.launch {
            val (filter, params) = getEntityListFilterInteractor.execute(entityListArgs.entityTypeSchema)
            navigationChannel.send(
                EntityListNavEvent.OnFilterSelectedEvent(
                    entityTypeSchema = entityListArgs.entityTypeSchema,
                    filter = filter,
                    params = params,
                    view = view
                )
            )
        }
    }

    override fun onSortClicked() {
        viewModelScope.launch {
            navigationChannel.send(
                EntityListNavEvent.OnSortSelectedEvent(
                    sort = getEntityListSortInteractor.execute(entityListArgs.entityTypeSchema)
                )
            )
        }
    }

    override fun onCreateEntityClicked(view: View) {
        viewModelScope.launch {
            navigationChannel.send(
                EntityListNavEvent.OnCreateEntityEvent(
                    entityListArgs.entityTypeSchema,
                    entityListArgs.parentEntityData,
                    view
                )
            )
        }
    }

    override fun onEntityCreated(createdEntity: FiberyEntityData) {
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

    override fun onError(error: Exception) {
        viewModelScope.launch {
            this@EntityListViewModelImpl.errorChannel.send(error)
        }
    }
}
