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
import androidx.paging.PagingData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.entitycreate.EntityCreateInteractor
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.Event
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.ToolbarViewState
import by.krossovochkin.fiberyunofficial.core.presentation.common.PaginatedListViewModelDelegate
import by.krossovochkin.fiberyunofficial.entitypicker.R
import by.krossovochkin.fiberyunofficial.entitypicker.domain.GetEntityListInteractor
import by.krossovochkin.fiberyunofficial.entitypicker.domain.GetEntityTypeSchemaInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EntityPickerViewModel(
    private val getEntityTypeSchemaInteractor: GetEntityTypeSchemaInteractor,
    getEntityListInteractor: GetEntityListInteractor,
    private val entityCreateInteractor: EntityCreateInteractor,
    private val entityPickerArgs: EntityPickerFragment.Args
) : ViewModel() {

    private val paginatedListDelegate = PaginatedListViewModelDelegate(
        viewModel = this,
        loadPage = { offset: Int, pageSize: Int ->
            getEntityListInteractor
                .execute(
                    entityPickerArgs.parentEntityData,
                    offset,
                    pageSize,
                    mutableSearchQuery.value.orEmpty()
                )
        },
        mapper = { entity ->
            EntityPickerItem(
                title = entity.title,
                entityData = entity
            )
        }
    )

    private val mutableError = MutableLiveData<Event<Exception>>()
    val error: LiveData<Event<Exception>> = mutableError

    private val mutableNavigation = MutableLiveData<Event<EntityPickerNavEvent>>()
    val navigation: LiveData<Event<EntityPickerNavEvent>> = mutableNavigation

    private val mutableSearchQuery = MutableLiveData("")

    private val mutableCreateButtonEnabledState = MutableLiveData(false)
    val entityCreateEnabled: LiveData<Boolean> = mutableCreateButtonEnabledState

    val entityItems: Flow<PagingData<ListItem>>
        get() = paginatedListDelegate.items

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
                    menuResId = R.menu.picker_entity_menu,
                    searchActionItemId = R.id.action_search
                )
            )
        }
    }

    fun select(item: ListItem) {
        if (item is EntityPickerItem) {
            onEntityPicked(item.entityData)
        } else {
            throw IllegalArgumentException()
        }
    }

    fun onBackPressed() {
        mutableNavigation.value = Event(EntityPickerNavEvent.BackEvent)
    }

    fun onSearchQueryChanged(query: String) {
        mutableSearchQuery.value = query
        mutableCreateButtonEnabledState.value = query.isNotEmpty()
        paginatedListDelegate.invalidate()
    }

    fun createEntity() {
        val name = mutableSearchQuery.value.orEmpty()
        require(name.isNotEmpty()) { "search query is empty" }

        viewModelScope.launch {
            val entityType = getEntityTypeSchemaInteractor
                .execute(entityPickerArgs.parentEntityData.fieldSchema)
            val entity = entityCreateInteractor.execute(
                entityTypeSchema = entityType,
                name = name
            )
            onEntityPicked(entity)
        }
    }

    fun onError(error: Exception) {
        mutableError.postValue(Event(error))
    }

    private fun onEntityPicked(entity: FiberyEntityData?) {
        mutableNavigation.postValue(
            Event(
                EntityPickerNavEvent.OnEntityPickedEvent(
                    parentEntityData = entityPickerArgs.parentEntityData,
                    entity = entity
                )
            )
        )
    }
}
