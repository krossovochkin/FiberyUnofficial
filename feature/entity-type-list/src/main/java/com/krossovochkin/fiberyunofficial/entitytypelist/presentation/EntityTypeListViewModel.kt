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
package com.krossovochkin.fiberyunofficial.entitytypelist.presentation

import android.view.View
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krossovochkin.core.presentation.color.ColorUtils
import com.krossovochkin.core.presentation.list.ListItem
import com.krossovochkin.core.presentation.list.ListViewModelDelegate
import com.krossovochkin.core.presentation.resources.NativeColor
import com.krossovochkin.core.presentation.resources.NativeText
import com.krossovochkin.core.presentation.ui.toolbar.ToolbarViewState
import com.krossovochkin.fiberyunofficial.entitytypelist.R
import com.krossovochkin.fiberyunofficial.entitytypelist.domain.GetEntityTypeListInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntityTypeListViewModel @Inject constructor(
    private val getEntityTypeListInteractor: GetEntityTypeListInteractor,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args: EntityTypeListFragmentArgs
        get() = EntityTypeListFragmentArgs.fromSavedStateHandle(savedStateHandle)

    val progress = MutableStateFlow(false)
    private val errorChannel = Channel<Exception>(Channel.BUFFERED)
    val error: Flow<Exception>
        get() = errorChannel.receiveAsFlow()
    private val navigationChannel = Channel<EntityTypeListNavEvent>(Channel.BUFFERED)
    val navigation: Flow<EntityTypeListNavEvent>
        get() = navigationChannel.receiveAsFlow()

    private val listDelegate = ListViewModelDelegate(
        viewModel = this,
        progress = progress,
        errorChannel = errorChannel,
        load = {
            getEntityTypeListInteractor.execute(args.fiberyApp)
                .map { entityType ->
                    EntityTypeListItem(
                        title = entityType.displayName,
                        badgeBgColor = ColorUtils.getColor(entityType.meta.uiColorHex),
                        entityTypeData = entityType
                    )
                }
        }
    )
    val entityTypeItems = listDelegate.items

    fun select(item: ListItem, itemView: View) {
        require(item is EntityTypeListItem)
        viewModelScope.launch {
            navigationChannel.send(
                EntityTypeListNavEvent.OnEntityTypeSelectedEvent(item.entityTypeData, itemView)
            )
        }
    }

    fun onBackPressed() {
        viewModelScope.launch {
            navigationChannel.send(EntityTypeListNavEvent.BackEvent)
        }
    }

    fun getToolbarViewState(): ToolbarViewState =
        ToolbarViewState(
            title = NativeText.Resource(R.string.entity_type_list_title),
            bgColor = NativeColor.Attribute(androidx.appcompat.R.attr.colorPrimary),
            hasBackButton = true
        )
}
