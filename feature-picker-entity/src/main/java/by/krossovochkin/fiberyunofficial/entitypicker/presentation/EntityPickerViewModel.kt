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
package by.krossovochkin.fiberyunofficial.entitypicker.presentation

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
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.ToolbarViewState
import by.krossovochkin.fiberyunofficial.entitypicker.R
import by.krossovochkin.fiberyunofficial.entitypicker.domain.GetEntityListInteractor
import by.krossovochkin.fiberyunofficial.entitypicker.domain.GetEntityTypeSchemaInteractor
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val PAGE_SIZE = 20

class EntityPickerViewModel(
    getEntityTypeSchemaInteractor: GetEntityTypeSchemaInteractor,
    getEntityListInteractor: GetEntityListInteractor,
    private val entityPickerArgs: EntityPickerFragment.Args
) : ViewModel() {

    private val mutableError = MutableLiveData<Event<Exception>>()
    val error: LiveData<Event<Exception>> = mutableError

    private val mutableNavigation = MutableLiveData<Event<EntityPickerNavEvent>>()
    val navigation: LiveData<Event<EntityPickerNavEvent>> = mutableNavigation

    private val mutableSearchQuery = MutableLiveData<String>()

    val entityItems: LiveData<PagedList<ListItem>>
        get() = entityItemsDatasourceFactory
            .map<ListItem> { entity ->
                EntityPickerItem(
                    title = entity.title,
                    entityData = entity
                )
            }
            .toLiveData(
                config = PagedList.Config.Builder()
                    .setPageSize(PAGE_SIZE)
                    .setEnablePlaceholders(false)
                    .build()
            )

    private val entityItemsDatasourceFactory: EntityPickerDataSourceFactory =
        EntityPickerDataSourceFactory(
            entityPickerArgs,
            getEntityListInteractor,
            mutableError,
            mutableSearchQuery
        )

    private val mutableToolbarViewState = MutableLiveData<ToolbarViewState>()
    val toolbarViewState: LiveData<ToolbarViewState> = mutableToolbarViewState

    init {
        viewModelScope.launch {
            val entityType = getEntityTypeSchemaInteractor
                .execute(entityPickerArgs.parentEntityData.fieldSchema)
            mutableToolbarViewState.postValue(
                ToolbarViewState(
                    title = entityType.displayName,
                    bgColorInt = ColorUtils.getColor(entityType.meta.uiColorHex),
                    hasBackButton = true,
                    menuResId = R.menu.menu_entity_picker,
                    searchActionItemId = R.id.action_search
                )
            )
        }
    }

    fun select(item: ListItem) {
        if (item is EntityPickerItem) {
            mutableNavigation.value = Event(
                EntityPickerNavEvent.OnEntityPickedEvent(
                    parentEntityData = entityPickerArgs.parentEntityData,
                    entity = item.entityData
                )
            )
        } else {
            throw IllegalArgumentException()
        }
    }

    fun onBackPressed() {
        mutableNavigation.value = Event(EntityPickerNavEvent.BackEvent)
    }

    fun onSearchQueryChanged(query: String) {
        mutableSearchQuery.value = query
        entityItemsDatasourceFactory.dataSource?.invalidate()
    }
}

private class EntityPickerDataSourceFactory(
    private val entityListArgs: EntityPickerFragment.Args,
    private val getEntityListInteractor: GetEntityListInteractor,
    private val mutableError: MutableLiveData<Event<Exception>>,
    private val searchQuery: LiveData<String>
) : DataSource.Factory<Int, FiberyEntityData>() {

    var dataSource: EntityPickerDataSource? = null
        private set

    override fun create(): DataSource<Int, FiberyEntityData> {
        val new = EntityPickerDataSource(
            entityListArgs,
            getEntityListInteractor,
            mutableError,
            searchQuery
        )
        dataSource = new
        return new
    }
}

private class EntityPickerDataSource(
    private val entityListArgs: EntityPickerFragment.Args,
    private val getEntityListInteractor: GetEntityListInteractor,
    private val mutableError: MutableLiveData<Event<Exception>>,
    private val searchQuery: LiveData<String>
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
                        entityListArgs.parentEntityData,
                        offset,
                        pageSize,
                        searchQuery.value.orEmpty()
                    )
            }
        } catch (e: Exception) {
            mutableError.postValue(Event(e))
            emptyList()
        }
    }
}
