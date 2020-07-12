/*
 *
 *    Copyright 2020 Vasya Drobushkov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *
 *
 */
package by.krossovochkin.fiberyunofficial.entitylist.presentation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.Event
import by.krossovochkin.fiberyunofficial.core.presentation.FabViewState
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.ResProvider
import by.krossovochkin.fiberyunofficial.core.presentation.ToolbarViewState
import by.krossovochkin.fiberyunofficial.entitylist.R
import by.krossovochkin.fiberyunofficial.entitylist.domain.AddEntityRelationInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListFilterInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListSortInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.RemoveEntityRelationInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListFilterInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListSortInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 20

class EntityListViewModel(
    getEntityListInteractor: GetEntityListInteractor,
    private val setEntityListFilterInteractor: SetEntityListFilterInteractor,
    private val setEntityListSortInteractor: SetEntityListSortInteractor,
    private val getEntityListFilterInteractor: GetEntityListFilterInteractor,
    private val getEntityListSortInteractor: GetEntityListSortInteractor,
    private val removeEntityRelationInteractor: RemoveEntityRelationInteractor,
    private val addEntityRelationInteractor: AddEntityRelationInteractor,
    private val resProvider: ResProvider,
    private val entityListArgs: EntityListFragment.Args
) : ViewModel() {

    private val mutableError = MutableLiveData<Event<Exception>>()
    val error: LiveData<Event<Exception>> = mutableError

    private val mutableNavigation = MutableLiveData<Event<EntityListNavEvent>>()
    val navigation: LiveData<Event<EntityListNavEvent>> = mutableNavigation

    private var dataSource: EntityListDataSource? = null
    private val pager = Pager(
        PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = false
        )
    ) {
        EntityListDataSource(entityListArgs, getEntityListInteractor)
            .also { dataSource = it }
    }

    val entityItems: Flow<PagingData<ListItem>>
        get() = pager
            .flow
            .map {
                it.map<ListItem> { entity ->
                    EntityListItem(
                        title = entity.title,
                        entityData = entity,
                        isRemoveAvailable = entityListArgs.parentEntityData != null
                    )
                }
            }
            .cachedIn(viewModelScope)

    val toolbarViewState: ToolbarViewState
        get() = ToolbarViewState(
            title = entityListArgs.entityTypeSchema.displayName,
            bgColorInt = ColorUtils.getColor(entityListArgs.entityTypeSchema.meta.uiColorHex),
            hasBackButton = true,
            menuResId = if (entityListArgs.parentEntityData == null) {
                R.menu.menu_entity_list
            } else {
                null
            }
        )

    fun getCreateFabViewState(context: Context) = FabViewState(
        bgColorInt = resProvider.getColorAttr(context, R.attr.colorPrimary)
    )

    fun select(item: ListItem) {
        if (item is EntityListItem) {
            mutableNavigation.value = Event(
                EntityListNavEvent.OnEntitySelectedEvent(item.entityData)
            )
        } else {
            throw IllegalArgumentException()
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
                dataSource?.invalidate()
            } catch (e: Exception) {
                mutableError.postValue(Event(e))
            }
        }
    }

    fun onBackPressed() {
        mutableNavigation.value = Event(EntityListNavEvent.BackEvent)
    }

    fun onFilterSelected(filter: String, params: String) {
        viewModelScope.launch {
            setEntityListFilterInteractor.execute(entityListArgs.entityTypeSchema, filter, params)
            dataSource?.invalidate()
        }
    }

    fun onSortSelected(sort: String) {
        viewModelScope.launch {
            setEntityListSortInteractor.execute(entityListArgs.entityTypeSchema, sort)
            dataSource?.invalidate()
        }
    }

    fun onFilterClicked() {
        viewModelScope.launch {
            val (filter, params) = getEntityListFilterInteractor.execute(entityListArgs.entityTypeSchema)
            mutableNavigation.value = Event(
                EntityListNavEvent.OnFilterSelectedEvent(
                    entityTypeSchema = entityListArgs.entityTypeSchema,
                    filter = filter,
                    params = params
                )
            )
        }
    }

    fun onSortClicked() {
        viewModelScope.launch {
            mutableNavigation.value = Event(
                EntityListNavEvent.OnSortSelectedEvent(
                    sort = getEntityListSortInteractor.execute(entityListArgs.entityTypeSchema)
                )
            )
        }
    }

    fun onCreateEntityClicked() {
        mutableNavigation.value = Event(
            EntityListNavEvent.OnCreateEntityEvent(
                entityListArgs.entityTypeSchema,
                entityListArgs.parentEntityData
            )
        )
    }

    fun onEntityCreated(
        createdEntity: FiberyEntityData
    ) {
        if (entityListArgs.parentEntityData == null) {
            dataSource?.invalidate()
            return
        }

        viewModelScope.launch {
            try {
                addEntityRelationInteractor
                    .execute(
                        parentEntityData = entityListArgs.parentEntityData,
                        childEntity = createdEntity
                    )
                dataSource?.invalidate()
            } catch (e: Exception) {
                mutableError.postValue(Event(e))
            }
        }
    }
}

private class EntityListDataSource(
    private val entityListArgs: EntityListFragment.Args,
    private val getEntityListInteractor: GetEntityListInteractor
) : PagingSource<Int, FiberyEntityData>() {

    private suspend fun loadPage(offset: Int, pageSize: Int): LoadResult<Int, FiberyEntityData> {
        return try {
            LoadResult.Page(
                data = getEntityListInteractor
                    .execute(
                        entityListArgs.entityTypeSchema,
                        offset,
                        pageSize,
                        entityListArgs.parentEntityData
                    ),
                prevKey = null,
                nextKey = offset + pageSize
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, FiberyEntityData> {
        return when (params) {
            is LoadParams.Refresh -> loadPage(0, params.loadSize)
            is LoadParams.Append -> loadPage(params.key, params.loadSize)
            is LoadParams.Prepend -> LoadResult.Error(UnsupportedOperationException())
        }
    }
}
