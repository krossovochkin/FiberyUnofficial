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
package com.krossovochkin.fiberyunofficial.entitypicker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.krossovochkin.core.presentation.list.ListItem
import com.krossovochkin.core.presentation.paging.PaginatedListViewModelDelegate
import com.krossovochkin.core.presentation.resources.NativeColor
import com.krossovochkin.core.presentation.resources.NativeText
import com.krossovochkin.core.presentation.ui.toolbar.ToolbarViewState
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.entitycreatedomain.EntityCreateInteractor
import com.krossovochkin.fiberyunofficial.entitypicker.R
import com.krossovochkin.fiberyunofficial.entitypicker.domain.GetEntityListInteractor
import com.krossovochkin.fiberyunofficial.entitypicker.domain.GetEntityTypeSchemaInteractor
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class EntityPickerViewModel @AssistedInject constructor(
    private val getEntityTypeSchemaInteractor: GetEntityTypeSchemaInteractor,
    getEntityListInteractor: GetEntityListInteractor,
    private val entityCreateInteractor: EntityCreateInteractor,
    @Assisted private val entityPickerArgs: EntityPickerFragment.Args
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(args: EntityPickerFragment.Args): EntityPickerViewModel
    }

    companion object {
        fun provideFactory(
            factory: Factory,
            args: EntityPickerFragment.Args
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return factory.create(args) as T
            }
        }
    }

    private val paginatedListDelegate = PaginatedListViewModelDelegate(
        viewModel = this,
        loadPage = { offset: Int, pageSize: Int ->
            getEntityListInteractor
                .execute(
                    entityPickerArgs.parentEntityData,
                    offset,
                    pageSize,
                    mutableSearchQuery.value
                )
        },
        mapper = { entity ->
            EntityPickerItem(
                title = entity.title,
                entityData = entity
            )
        }
    )

    private val errorChannel = Channel<Exception>(Channel.BUFFERED)
    val error: Flow<Exception>
        get() = errorChannel.receiveAsFlow()

    private val navigationChannel = Channel<EntityPickerNavEvent>(Channel.BUFFERED)
    val navigation: Flow<EntityPickerNavEvent>
        get() = navigationChannel.receiveAsFlow()

    private val mutableSearchQuery = MutableStateFlow("")

    val entityCreateEnabled = MutableStateFlow(false)

    val entityItems: Flow<PagingData<ListItem>>
        get() = paginatedListDelegate.items

    val toolbarViewState = flow {
        val entityType = getEntityTypeSchemaInteractor
            .execute(entityPickerArgs.parentEntityData.fieldSchema)
        emit(
            ToolbarViewState(
                title = NativeText.Simple(entityType.displayName),
                bgColor = NativeColor.Hex(entityType.meta.uiColorHex),
                hasBackButton = true,
                menuResId = R.menu.picker_entity_menu,
                searchActionItemId = R.id.action_search
            )
        )
    }

    fun select(item: ListItem) {
        require(item is EntityPickerItem)
        onEntityPicked(item.entityData)
    }

    fun onBackPressed() {
        viewModelScope.launch {
            navigationChannel.send(
                EntityPickerNavEvent.BackEvent
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        mutableSearchQuery.value = query
        viewModelScope.launch {
            entityCreateEnabled.emit(query.isNotEmpty())
        }
        paginatedListDelegate.invalidate()
    }

    fun createEntity() {
        val name = mutableSearchQuery.value
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
        viewModelScope.launch {
            this@EntityPickerViewModel.errorChannel.send(error)
        }
    }

    private fun onEntityPicked(entity: FiberyEntityData?) {
        viewModelScope.launch {
            navigationChannel.send(
                EntityPickerNavEvent.OnEntityPickedEvent(
                    parentEntityData = entityPickerArgs.parentEntityData,
                    entity = entity
                )
            )
        }
    }
}
