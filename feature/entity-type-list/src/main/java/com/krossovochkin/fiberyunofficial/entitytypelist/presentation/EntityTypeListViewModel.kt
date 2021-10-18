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

import android.content.Context
import android.view.View
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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class EntityTypeListViewModel : ViewModel() {

    abstract val progress: Flow<Boolean>

    abstract val error: Flow<Exception>

    abstract val navigation: Flow<EntityTypeListNavEvent>

    abstract val entityTypeItems: Flow<List<ListItem>>

    abstract fun getToolbarViewState(context: Context): ToolbarViewState

    abstract fun select(item: ListItem, itemView: View)

    abstract fun onBackPressed()
}

internal class EntityTypeListViewModelImpl(
    private val getEntityTypeListInteractor: GetEntityTypeListInteractor,
    private val args: EntityTypeListFragment.Args,
) : EntityTypeListViewModel() {

    override val progress = MutableStateFlow(false)
    private val errorChannel = Channel<Exception>(Channel.BUFFERED)
    override val error: Flow<Exception>
        get() = errorChannel.receiveAsFlow()
    private val navigationChannel = Channel<EntityTypeListNavEvent>(Channel.BUFFERED)
    override val navigation: Flow<EntityTypeListNavEvent>
        get() = navigationChannel.receiveAsFlow()

    private val listDelegate = ListViewModelDelegate(
        viewModel = this,
        progress = progress,
        errorChannel = errorChannel,
        load = {
            getEntityTypeListInteractor.execute(args.fiberyAppData)
                .map { entityType ->
                    EntityTypeListItem(
                        title = entityType.displayName,
                        badgeBgColor = ColorUtils.getColor(entityType.meta.uiColorHex),
                        entityTypeData = entityType
                    )
                }
        }
    )
    override val entityTypeItems = listDelegate.items

    override fun select(item: ListItem, itemView: View) {
        if (item is EntityTypeListItem) {
            viewModelScope.launch {
                navigationChannel.send(
                    EntityTypeListNavEvent.OnEntityTypeSelectedEvent(item.entityTypeData, itemView)
                )
            }
        } else {
            throw IllegalArgumentException()
        }
    }

    override fun onBackPressed() {
        viewModelScope.launch {
            navigationChannel.send(EntityTypeListNavEvent.BackEvent)
        }
    }

    override fun getToolbarViewState(context: Context): ToolbarViewState =
        ToolbarViewState(
            title = NativeText.Resource(R.string.entity_type_list_title),
            bgColor = NativeColor.Attribute(R.attr.colorPrimary),
            hasBackButton = true
        )
}
