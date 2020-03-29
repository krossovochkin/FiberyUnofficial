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
package by.krossovochkin.fiberyunofficial.entitylist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.krossovochkin.fiberyunofficial.entitylist.domain.AddEntityRelationInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListFilterInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListSortInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.RemoveEntityRelationInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListFilterInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListSortInteractor

class EntityListViewModelFactory(
    private val getEntityListInteractor: GetEntityListInteractor,
    private val setEntityListFilterInteractor: SetEntityListFilterInteractor,
    private val setEntityListSortInteractor: SetEntityListSortInteractor,
    private val getEntityListFilterInteractor: GetEntityListFilterInteractor,
    private val getEntityListSortInteractor: GetEntityListSortInteractor,
    private val removeEntityRelationInteractor: RemoveEntityRelationInteractor,
    private val addEntityRelationInteractor: AddEntityRelationInteractor,
    private val entityListArgs: EntityListFragment.Args
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == EntityListViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            EntityListViewModel(
                getEntityListInteractor,
                setEntityListFilterInteractor,
                setEntityListSortInteractor,
                getEntityListFilterInteractor,
                getEntityListSortInteractor,
                removeEntityRelationInteractor,
                addEntityRelationInteractor,
                entityListArgs
            ) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
