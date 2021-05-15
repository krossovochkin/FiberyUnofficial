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
import com.krossovochkin.fiberyunofficial.core.domain.entitycreate.EntityCreateInteractor
import com.krossovochkin.fiberyunofficial.core.presentation.ResProvider

class EntityCreateViewModelFactory(
    private val entityCreateArgs: EntityCreateFragment.Args,
    private val entityCreateInteractor: EntityCreateInteractor,
    private val resProvider: ResProvider
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == EntityCreateViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            EntityCreateViewModelImpl(
                entityCreateArgs,
                entityCreateInteractor,
                resProvider
            ) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
