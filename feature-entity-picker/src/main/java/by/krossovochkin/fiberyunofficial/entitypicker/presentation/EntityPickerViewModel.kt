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
        EntityPickerDataSourceFactory(entityPickerArgs, getEntityListInteractor, mutableError)

    private val mutableToolbarViewState = MutableLiveData<EntityPickerToolbarViewState>()
    val toolbarViewState: LiveData<EntityPickerToolbarViewState> = mutableToolbarViewState

    init {
        viewModelScope.launch {
            val entityType = getEntityTypeSchemaInteractor.execute(entityPickerArgs.fieldSchema)
            mutableToolbarViewState.postValue(
                EntityPickerToolbarViewState(
                    title = entityType.displayName,
                    bgColorInt = ColorUtils.getColor(entityType.meta.uiColorHex)
                )
            )
        }
    }

    fun select(item: ListItem) {
        if (item is EntityPickerItem) {
            mutableNavigation.value = Event(
                EntityPickerNavEvent.OnEntityPickedEvent(
                    fieldSchema = entityPickerArgs.fieldSchema,
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
}

private class EntityPickerDataSourceFactory(
    private val entityListArgs: EntityPickerFragment.Args,
    private val getEntityListInteractor: GetEntityListInteractor,
    private val mutableError: MutableLiveData<Event<Exception>>
) : DataSource.Factory<Int, FiberyEntityData>() {

    var dataSource: EntityPickerDataSource? = null
        private set

    override fun create(): DataSource<Int, FiberyEntityData> {
        val new = EntityPickerDataSource(entityListArgs, getEntityListInteractor, mutableError)
        dataSource = new
        return new
    }
}

private class EntityPickerDataSource(
    private val entityListArgs: EntityPickerFragment.Args,
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
                        entityListArgs.fieldSchema,
                        offset,
                        pageSize
                    )
            }
        } catch (e: Exception) {
            mutableError.postValue(Event(e))
            emptyList()
        }
    }
}