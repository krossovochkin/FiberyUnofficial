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
package com.krossovochkin.fiberyunofficial.entitylist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krossovochkin.fiberyunofficial.core.presentation.ResProvider
import com.krossovochkin.fiberyunofficial.entitylist.domain.AddEntityRelationInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListFilterInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListSortInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.RemoveEntityRelationInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListFilterInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListSortInteractor

class EntityListViewModelFactory(
    private val getEntityListInteractor: GetEntityListInteractor,
    private val setEntityListFilterInteractor: SetEntityListFilterInteractor,
    private val setEntityListSortInteractor: SetEntityListSortInteractor,
    private val getEntityListFilterInteractor: GetEntityListFilterInteractor,
    private val getEntityListSortInteractor: GetEntityListSortInteractor,
    private val removeEntityRelationInteractor: RemoveEntityRelationInteractor,
    private val addEntityRelationInteractor: AddEntityRelationInteractor,
    private val resProvider: ResProvider,
    private val entityListArgs: EntityListFragment.Args
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass == EntityListViewModel::class.java) {
            EntityListViewModelImpl(
                getEntityListInteractor,
                setEntityListFilterInteractor,
                setEntityListSortInteractor,
                getEntityListFilterInteractor,
                getEntityListSortInteractor,
                removeEntityRelationInteractor,
                addEntityRelationInteractor,
                resProvider,
                entityListArgs
            ) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
