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
import androidx.lifecycle.viewModelScope
import com.krossovochkin.fiberyunofficial.core.domain.entitycreate.EntityCreateInteractor
import com.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import com.krossovochkin.fiberyunofficial.core.presentation.ResProvider
import com.krossovochkin.fiberyunofficial.core.presentation.ToolbarViewState
import com.krossovochkin.fiberyunofficial.entitycreate.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class EntityCreateViewModel : ViewModel() {

    abstract val error: Flow<Exception>

    abstract val navigation: Flow<EntityCreateNavEvent>

    abstract val toolbarViewState: ToolbarViewState

    abstract fun createEntity(name: String)
}

internal class EntityCreateViewModelImpl(
    private val entityCreateArgs: EntityCreateFragment.Args,
    private val entityCreateInteractor: EntityCreateInteractor,
    private val resProvider: ResProvider
) : EntityCreateViewModel() {

    private val errorChannel = Channel<Exception>(Channel.BUFFERED)
    override val error: Flow<Exception>
        get() = errorChannel.receiveAsFlow()
    private val navigationChannel = Channel<EntityCreateNavEvent>(Channel.BUFFERED)
    override val navigation: Flow<EntityCreateNavEvent>
        get() = navigationChannel.receiveAsFlow()

    override val toolbarViewState: ToolbarViewState
        get() = ToolbarViewState(
            title = resProvider.getString(
                R.string.entity_create_toolbar_title,
                entityCreateArgs.entityTypeSchema.displayName
            ),
            bgColorInt = ColorUtils.getColor(entityCreateArgs.entityTypeSchema.meta.uiColorHex),
            hasBackButton = true
        )

    override fun createEntity(name: String) {
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
