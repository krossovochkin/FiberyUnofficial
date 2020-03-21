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
package by.krossovochkin.fiberyunofficial.applist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.krossovochkin.fiberyunofficial.applist.domain.GetAppListInteractor
import by.krossovochkin.fiberyunofficial.core.presentation.Event
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import kotlinx.coroutines.launch

class AppListViewModel(
    private val getAppListInteractor: GetAppListInteractor
) : ViewModel() {

    private val mutableAppItems = MutableLiveData<List<ListItem>>()
    val appItems: LiveData<List<ListItem>> = mutableAppItems

    private val mutableProgress = MutableLiveData<Boolean>()
    val progress: LiveData<Boolean> = mutableProgress

    private val mutableError = MutableLiveData<Event<Exception>>()
    val error: LiveData<Event<Exception>> = mutableError

    private val mutableNavigation = MutableLiveData<Event<AppListNavEvent>>()
    val navigation: LiveData<Event<AppListNavEvent>> = mutableNavigation

    init {
        viewModelScope.launch {
            try {
                mutableProgress.value = true
                mutableAppItems.value = getAppListInteractor.execute()
                    .map { app ->
                        AppListItem(
                            title = app.name,
                            appData = app
                        )
                    }
            } catch (e: Exception) {
                mutableError.value = Event(e)
            } finally {
                mutableProgress.value = false
            }
        }
    }

    fun select(item: ListItem) {
        if (item is AppListItem) {
            mutableNavigation.value = Event(
                AppListNavEvent.OnAppSelectedEvent(item.appData)
            )
        } else {
            throw IllegalArgumentException()
        }
    }
}
