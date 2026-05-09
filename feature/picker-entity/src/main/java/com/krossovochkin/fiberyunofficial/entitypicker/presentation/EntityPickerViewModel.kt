/*
   Copyright 2020 Vasya Drobushkov

   Licensed under the Apache License, Version 2.0 (the \"License\");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an \"AS IS\" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package com.krossovochkin.fiberyunofficial.entitypicker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.krossovochkin.core.presentation.resources.NativeColor
import com.krossovochkin.core.presentation.resources.NativeText
import com.krossovochkin.core.presentation.ui.toolbar.ToolbarViewState
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData
import com.krossovochkin.fiberyunofficial.entitycreatedomain.EntityCreateInteractor
import com.krossovochkin.fiberyunofficial.entitypicker.domain.GetEntityListInteractor
import com.krossovochkin.fiberyunofficial.entitypicker.domain.GetEntityTypeSchemaInteractor
import com.krossovochkin.fiberyunofficial.navigation.EntityPickerNavKey
import com.krossovochkin.fiberyunofficial.ui.list.ListItem
import com.krossovochkin.fiberyunofficial.ui.paging.PaginatedListViewModelDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@HiltViewModel(assistedFactory = EntityPickerViewModel.Factory::class)
class EntityPickerViewModel @AssistedInject constructor(
    private val getEntityTypeSchemaInteractor: GetEntityTypeSchemaInteractor,
    getEntityListInteractor: GetEntityListInteractor,
    private val entityCreateInteractor: EntityCreateInteractor,
    @Assisted private val entityPickerArgs: EntityPickerNavKey,
) : ViewModel() {

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
                hasBackButton = true
            )

        )
    }

    fun select(item: ListItem) {
        require(item is EntityPickerItem)
    }

    fun onSearchQueryChanged(query: String) {
        mutableSearchQuery.value = query
        viewModelScope.launch {
            entityCreateEnabled.emit(query.isNotEmpty())
        }
        paginatedListDelegate.invalidate()
    }

    fun createEntity(onEntityPicked: (ParentEntityData, FiberyEntityData?) -> Unit) {
        val name = mutableSearchQuery.value
        require(name.isNotEmpty()) { "search query is empty" }

        viewModelScope.launch {
            val entityType = getEntityTypeSchemaInteractor
                .execute(entityPickerArgs.parentEntityData.fieldSchema)
            val entity = entityCreateInteractor.execute(
                entityTypeSchema = entityType,
                name = name
            )
            onEntityPicked(entityPickerArgs.parentEntityData, entity)
        }
    }

    fun onError(error: Exception) {
        if (error is CancellationException) {
            return
        }
        viewModelScope.launch {
            this@EntityPickerViewModel.errorChannel.send(error)
        }
    }

    fun getParentEntityData() = entityPickerArgs.parentEntityData

    @AssistedFactory
    interface Factory {
        fun create(
            args: EntityPickerNavKey,
        ): EntityPickerViewModel
    }
}
