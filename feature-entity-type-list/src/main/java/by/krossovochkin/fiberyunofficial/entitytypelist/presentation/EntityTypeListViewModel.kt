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
package by.krossovochkin.fiberyunofficial.entitytypelist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.Event
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.entitytypelist.domain.GetEntityTypeListInteractor
import kotlinx.coroutines.launch

class EntityTypeListViewModel(
    private val getEntityTypeListInteractor: GetEntityTypeListInteractor,
    private val args: EntityTypeListFragment.Args
) : ViewModel() {

    private val mutableEntityTypeItems = MutableLiveData<List<ListItem>>()
    val entityTypeItems: LiveData<List<ListItem>> = mutableEntityTypeItems

    private val mutableProgress = MutableLiveData<Boolean>()
    val progress: LiveData<Boolean> = mutableProgress

    private val mutableError = MutableLiveData<Event<Exception>>()
    val error: LiveData<Event<Exception>> = mutableError

    private val mutableNavigation = MutableLiveData<Event<EntityTypeListNavEvent>>()
    val navigation: LiveData<Event<EntityTypeListNavEvent>> = mutableNavigation

    init {
        viewModelScope.launch {
            try {
                mutableProgress.value = true
                mutableEntityTypeItems.value =
                    getEntityTypeListInteractor.execute(args.fiberyAppData)
                        .map { entityType ->
                            EntityTypeListItem(
                                title = entityType.displayName,
                                badgeBgColor = ColorUtils.getColor(entityType.meta.uiColorHex),
                                entityTypeData = entityType
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
        if (item is EntityTypeListItem) {
            mutableNavigation.value = Event(
                EntityTypeListNavEvent.OnEntityTypeSelectedEvent(item.entityTypeData)
            )
        } else {
            throw IllegalArgumentException()
        }
    }

    fun onBackPressed() {
        mutableNavigation.value = Event(EntityTypeListNavEvent.BackEvent)
    }
}
