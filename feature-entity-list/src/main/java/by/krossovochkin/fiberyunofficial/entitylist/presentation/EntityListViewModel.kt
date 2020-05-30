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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.PositionalDataSource
import androidx.paging.toLiveData
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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

    val entityItems: LiveData<PagedList<ListItem>>
        get() = entityItemsDatasourceFactory
            .map<ListItem> { entity ->
                EntityListItem(
                    title = entity.title,
                    entityData = entity,
                    isRemoveAvailable = entityListArgs.parentEntityData != null
                )
            }
            .toLiveData(
                config = PagedList.Config.Builder()
                    .setPageSize(PAGE_SIZE)
                    .setEnablePlaceholders(false)
                    .build()
            )

    private val entityItemsDatasourceFactory: EntityListDataSourceFactory =
        EntityListDataSourceFactory(entityListArgs, getEntityListInteractor, mutableError)

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
                entityItemsDatasourceFactory.dataSource?.invalidate()
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
            entityItemsDatasourceFactory.dataSource?.invalidate()
        }
    }

    fun onSortSelected(sort: String) {
        viewModelScope.launch {
            setEntityListSortInteractor.execute(entityListArgs.entityTypeSchema, sort)
            entityItemsDatasourceFactory.dataSource?.invalidate()
        }
    }

    fun onFilterClicked() {
        viewModelScope.launch {
            val (filter, params) = getEntityListFilterInteractor.execute(entityListArgs.entityTypeSchema)
            mutableNavigation.value = Event(
                EntityListNavEvent.OnFilterSelectedEvent(
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
            entityItemsDatasourceFactory.dataSource?.invalidate()
            return
        }

        viewModelScope.launch {
            try {
                addEntityRelationInteractor
                    .execute(
                        parentEntityData = entityListArgs.parentEntityData,
                        childEntity = createdEntity
                    )
                entityItemsDatasourceFactory.dataSource?.invalidate()
            } catch (e: Exception) {
                mutableError.postValue(Event(e))
            }
        }
    }
}

private class EntityListDataSourceFactory(
    private val entityListArgs: EntityListFragment.Args,
    private val getEntityListInteractor: GetEntityListInteractor,
    private val mutableError: MutableLiveData<Event<Exception>>
) : DataSource.Factory<Int, FiberyEntityData>() {

    var dataSource: EntityListDataSource? = null
        private set

    override fun create(): DataSource<Int, FiberyEntityData> {
        val new = EntityListDataSource(entityListArgs, getEntityListInteractor, mutableError)
        dataSource = new
        return new
    }
}

private class EntityListDataSource(
    private val entityListArgs: EntityListFragment.Args,
    private val getEntityListInteractor: GetEntityListInteractor,
    private val mutableError: MutableLiveData<Event<Exception>>
) : PositionalDataSource<FiberyEntityData>() {

    override fun loadRange(
        params: LoadRangeParams,
        callback: LoadRangeCallback<FiberyEntityData>
    ) {
        val offset = params.startPosition
        val size = params.loadSize

        callback.onResult(loadPage(offset, size))
    }

    override fun loadInitial(
        params: LoadInitialParams,
        callback: LoadInitialCallback<FiberyEntityData>
    ) {
        val offset = params.requestedStartPosition
        val size = params.requestedLoadSize

        callback.onResult(loadPage(offset, size), offset)
    }

    private fun loadPage(offset: Int, pageSize: Int): List<FiberyEntityData> {
        return try {
            runBlocking {
                getEntityListInteractor
                    .execute(
                        entityListArgs.entityTypeSchema,
                        offset,
                        pageSize,
                        entityListArgs.parentEntityData
                    )
            }
        } catch (e: Exception) {
            mutableError.postValue(Event(e))
            emptyList()
        }
    }
}
