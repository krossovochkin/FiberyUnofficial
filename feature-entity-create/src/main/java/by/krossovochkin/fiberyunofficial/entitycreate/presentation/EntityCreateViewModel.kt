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
package by.krossovochkin.fiberyunofficial.entitycreate.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.Event
import by.krossovochkin.fiberyunofficial.core.presentation.ResProvider
import by.krossovochkin.fiberyunofficial.core.presentation.ToolbarViewState
import by.krossovochkin.fiberyunofficial.entitycreate.R
import by.krossovochkin.fiberyunofficial.entitycreate.domain.EntityCreateInteractor
import kotlinx.coroutines.launch

class EntityCreateViewModel(
    private val entityCreateArgs: EntityCreateFragment.Args,
    private val entityCreateInteractor: EntityCreateInteractor,
    private val resProvider: ResProvider
) : ViewModel() {

    private val mutableNavigation = MutableLiveData<Event<EntityCreateNavEvent>>()
    val navigation: LiveData<Event<EntityCreateNavEvent>> = mutableNavigation

    private val mutableError = MutableLiveData<Event<Exception>>()
    val error: LiveData<Event<Exception>> = mutableError

    val toolbarViewState: ToolbarViewState
        get() = ToolbarViewState(
            title = resProvider.getString(
                R.string.toolbar_title_create, entityCreateArgs.entityTypeSchema.displayName
            ),
            bgColorInt = ColorUtils.getColor(entityCreateArgs.entityTypeSchema.meta.uiColorHex),
            hasBackButton = true
        )

    fun createEntity(name: String) {
        viewModelScope.launch {
            try {
                val id = entityCreateInteractor
                    .execute(entityCreateArgs.entityTypeSchema, name)
                mutableNavigation.postValue(
                    Event(
                        EntityCreateNavEvent.OnEntityCreateSuccessEvent(
                            createdEntityId = id
                        )
                    )
                )
            } catch (e: Exception) {
                mutableError.postValue(Event(e))
            }
        }
    }
}
