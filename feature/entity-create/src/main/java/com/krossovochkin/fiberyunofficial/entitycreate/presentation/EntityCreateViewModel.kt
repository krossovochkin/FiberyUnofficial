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
package com.krossovochkin.fiberyunofficial.entitycreate.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.krossovochkin.core.presentation.resources.NativeColor
import com.krossovochkin.core.presentation.resources.NativeText
import com.krossovochkin.core.presentation.ui.toolbar.ToolbarViewState
import com.krossovochkin.fiberyunofficial.entitycreate.R
import com.krossovochkin.fiberyunofficial.entitycreatedomain.EntityCreateInteractor
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class EntityCreateViewModel @AssistedInject constructor(
    private val entityCreateInteractor: EntityCreateInteractor,
    @Assisted private val entityCreateArgs: EntityCreateFragment.Args,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(entityCreateArgs: EntityCreateFragment.Args): EntityCreateViewModel
    }

    companion object {
        fun provideFactory(
            factory: Factory,
            args: EntityCreateFragment.Args
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return factory.create(args) as T
            }
        }
    }

    private val errorChannel = Channel<Exception>(Channel.BUFFERED)
    val error: Flow<Exception>
        get() = errorChannel.receiveAsFlow()
    private val navigationChannel = Channel<EntityCreateNavEvent>(Channel.BUFFERED)
    val navigation: Flow<EntityCreateNavEvent>
        get() = navigationChannel.receiveAsFlow()

    val toolbarViewState: ToolbarViewState
        get() = ToolbarViewState(
            title = NativeText.Arguments(
                R.string.entity_create_toolbar_title,
                entityCreateArgs.entityTypeSchema.displayName
            ),
            bgColor = NativeColor.Hex(entityCreateArgs.entityTypeSchema.meta.uiColorHex),
            hasBackButton = true
        )

    fun createEntity(name: String) {
        viewModelScope.launch {
            try {
                val createdEntity = entityCreateInteractor
                    .execute(entityCreateArgs.entityTypeSchema, name)
                navigationChannel.send(
                    EntityCreateNavEvent.OnEntityCreateSuccessEvent(
                        createdEntity = createdEntity
                    )
                )
            } catch (e: Exception) {
                errorChannel.send(e)
            }
        }
    }
}
