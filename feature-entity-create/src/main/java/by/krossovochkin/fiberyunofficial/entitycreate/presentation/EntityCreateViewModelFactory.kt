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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.krossovochkin.fiberyunofficial.core.presentation.ResProvider
import by.krossovochkin.fiberyunofficial.entitycreate.domain.EntityCreateInteractor

class EntityCreateViewModelFactory(
    private val entityCreateArgs: EntityCreateFragment.Args,
    private val entityCreateInteractor: EntityCreateInteractor,
    private val resProvider: ResProvider
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == EntityCreateViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            EntityCreateViewModel(
                entityCreateArgs,
                entityCreateInteractor,
                resProvider
            ) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
