package by.krossovochkin.fiberyunofficial.entitylist.presentation

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
import by.krossovochkin.fiberyunofficial.entitylist.R
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListFilterInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListSortInteractor
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
                    entityData = entity
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

    val toolbarViewState: EntityListToolbarViewState
        get() = EntityListToolbarViewState(
            title = entityListArgs.entityTypeSchema.displayName,
            bgColorInt = ColorUtils.getColor(entityListArgs.entityTypeSchema.meta.uiColorHex),
            menuResId = if (entityListArgs.entityParams == null) {
                R.menu.menu_entity_list
            } else {
                null
            }
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
                entityListArgs.entityParams
            )
        )
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
                        entityListArgs.entityParams
                    )
            }
        } catch (e: Exception) {
            mutableError.postValue(Event(e))
            emptyList()
        }
    }
}
