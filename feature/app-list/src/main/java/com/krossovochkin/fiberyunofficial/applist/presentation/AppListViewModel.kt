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

import androidx.lifecycle.ViewModel
import com.krossovochkin.core.presentation.resources.NativeColor
import com.krossovochkin.core.presentation.resources.NativeText
import com.krossovochkin.core.presentation.ui.toolbar.ToolbarViewState
import com.krossovochkin.fiberyunofficial.applist.R
import com.krossovochkin.fiberyunofficial.applist.domain.GetAppListInteractor
import com.krossovochkin.fiberyunofficial.ui.list.ListItem
import com.krossovochkin.fiberyunofficial.ui.list.ListViewModelDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@HiltViewModel(assistedFactory = AppListViewModel.Factory::class)
class AppListViewModel @AssistedInject constructor(
    private val getAppListInteractor: GetAppListInteractor,
) : ViewModel() {

    val progress = MutableStateFlow(false)
    private val errorChannel = Channel<Exception>(Channel.BUFFERED)
    val error
        get() = errorChannel.receiveAsFlow()

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
    val appItems: Flow<List<ListItem>> = listDelegate.items

    fun getToolbarViewState(): ToolbarViewState =
        ToolbarViewState(
            title = NativeText.Resource(R.string.app_list_title),
            bgColor = NativeColor.Attribute(androidx.appcompat.R.attr.colorPrimary)
        )

    @AssistedFactory
    interface Factory {
        fun create(): AppListViewModel
    }
}
