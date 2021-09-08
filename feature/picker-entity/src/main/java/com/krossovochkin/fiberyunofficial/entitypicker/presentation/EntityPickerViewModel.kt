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
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.core.domain.entitycreate.EntityCreateInteractor
import com.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import com.krossovochkin.fiberyunofficial.core.presentation.ListItem
import com.krossovochkin.fiberyunofficial.core.presentation.ToolbarViewState
import com.krossovochkin.fiberyunofficial.core.presentation.common.PaginatedListViewModelDelegate
import com.krossovochkin.fiberyunofficial.entitypicker.R
import com.krossovochkin.fiberyunofficial.entitypicker.domain.GetEntityListInteractor
import com.krossovochkin.fiberyunofficial.entitypicker.domain.GetEntityTypeSchemaInteractor
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class EntityPickerViewModel : ViewModel() {

    abstract val error: Flow<Exception>

    abstract val navigation: Flow<EntityPickerNavEvent>

    abstract val entityCreateEnabled: Flow<Boolean>

    abstract val entityItems: Flow<PagingData<ListItem>>

    abstract val toolbarViewState: Flow<ToolbarViewState>

    abstract fun select(item: ListItem)

    abstract fun onBackPressed()

    abstract fun onSearchQueryChanged(query: String)

    abstract fun createEntity()

    abstract fun onError(error: Exception)
}

internal class EntityPickerViewModelImpl(
    private val getEntityTypeSchemaInteractor: GetEntityTypeSchemaInteractor,
    getEntityListInteractor: GetEntityListInteractor,
    private val entityCreateInteractor: EntityCreateInteractor,
    private val entityPickerArgs: EntityPickerFragment.Args
) : EntityPickerViewModel() {

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
    override val error: Flow<Exception>
        get() = errorChannel.receiveAsFlow()

    private val navigationChannel = Channel<EntityPickerNavEvent>(Channel.BUFFERED)
    override val navigation: Flow<EntityPickerNavEvent>
        get() = navigationChannel.receiveAsFlow()

    private val mutableSearchQuery = MutableStateFlow("")

    override val entityCreateEnabled = MutableStateFlow(false)

    override val entityItems: Flow<PagingData<ListItem>>
        get() = paginatedListDelegate.items

    override val toolbarViewState = flow {
        val entityType = getEntityTypeSchemaInteractor
            .execute(entityPickerArgs.parentEntityData.fieldSchema)
        emit(
            ToolbarViewState(
                title = entityType.displayName,
                bgColorInt = ColorUtils.getColor(
                    entityType.meta.uiColorHex
                ),
                hasBackButton = true,
                menuResId = R.menu.picker_entity_menu,
                searchActionItemId = R.id.action_search
            )
        )
    }

    override fun select(item: ListItem) {
        if (item is EntityPickerItem) {
            onEntityPicked(item.entityData)
        } else {
            throw IllegalArgumentException()
        }
    }

    override fun onBackPressed() {
        viewModelScope.launch {
            navigationChannel.send(
                EntityPickerNavEvent.BackEvent
            )
        }
    }

    override fun onSearchQueryChanged(query: String) {
        mutableSearchQuery.value = query
        viewModelScope.launch {
            entityCreateEnabled.emit(query.isNotEmpty())
        }
        paginatedListDelegate.invalidate()
    }

    override fun createEntity() {
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

    override fun onError(error: Exception) {
        viewModelScope.launch {
            this@EntityPickerViewModelImpl.errorChannel.send(error)
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
