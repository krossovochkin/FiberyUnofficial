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
package com.krossovochkin.fiberyunofficial.applist.presentation

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krossovochkin.fiberyunofficial.applist.R
import com.krossovochkin.fiberyunofficial.applist.domain.GetAppListInteractor
import com.krossovochkin.fiberyunofficial.core.presentation.ListItem
import com.krossovochkin.fiberyunofficial.core.presentation.ResProvider
import com.krossovochkin.fiberyunofficial.core.presentation.ToolbarViewState
import com.krossovochkin.fiberyunofficial.core.presentation.common.ListViewModelDelegate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class AppListViewModel : ViewModel() {

    abstract val progress: Flow<Boolean>

    abstract val error: Flow<Exception>

    abstract val navigation: Flow<AppListNavEvent>

    abstract val appItems: Flow<List<ListItem>>

    abstract fun getToolbarViewState(context: Context): ToolbarViewState

    abstract fun select(item: ListItem, itemView: View)
}

internal class AppListViewModelImpl(
    private val getAppListInteractor: GetAppListInteractor,
    private val resProvider: ResProvider
) : AppListViewModel() {

    override val progress = MutableStateFlow(false)
    private val errorChannel = Channel<Exception>(Channel.BUFFERED)
    override val error
        get() = errorChannel.receiveAsFlow()
    private val navigationChannel = Channel<AppListNavEvent>(Channel.BUFFERED)
    override val navigation: Flow<AppListNavEvent>
        get() = navigationChannel.receiveAsFlow()

    private val listDelegate = ListViewModelDelegate(
        viewModel = this,
        progress = progress,
        errorChannel = errorChannel,
        load = {
            getAppListInteractor.execute()
                .map { app ->
                    AppListItem(
                        title = app.name,
                        appData = app
                    )
                }
        }
    )
    override val appItems: Flow<List<ListItem>> = listDelegate.items

    override fun select(item: ListItem, itemView: View) {
        if (item is AppListItem) {
            viewModelScope.launch {
                navigationChannel.send(AppListNavEvent.OnAppSelectedEvent(item.appData, itemView))
            }
        } else {
            throw IllegalArgumentException()
        }
    }

    override fun getToolbarViewState(context: Context): ToolbarViewState = ToolbarViewState(
        title = resProvider.getString(R.string.app_list_title),
        bgColorInt = resProvider.getColorAttr(context, R.attr.colorPrimary)
    )
}
