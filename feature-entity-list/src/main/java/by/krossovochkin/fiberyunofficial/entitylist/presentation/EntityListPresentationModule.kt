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

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import by.krossovochkin.fiberyunofficial.entitylist.domain.AddEntityRelationInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListFilterInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListSortInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.RemoveEntityRelationInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListFilterInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListSortInteractor
import dagger.Module
import dagger.Provides

@Module
object EntityListPresentationModule {

    @JvmStatic
    @Provides
    fun entityListArgs(
        fragment: Fragment,
        entityListArgsProvider: EntityListFragment.ArgsProvider
    ): EntityListFragment.Args {
        return entityListArgsProvider.getEntityListArgs(fragment.requireArguments())
    }

    @JvmStatic
    @Provides
    fun entityListViewModel(
        fragment: Fragment,
        entityListViewModelFactory: EntityListViewModelFactory
    ): EntityListViewModel {
        return ViewModelProvider(fragment, entityListViewModelFactory).get()
    }

    @Suppress("LongParameterList")
    @JvmStatic
    @Provides
    fun entityListViewModelFactory(
        getEntityListInteractor: GetEntityListInteractor,
        setEntityListFilterInteractor: SetEntityListFilterInteractor,
        setEntityListSortInteractor: SetEntityListSortInteractor,
        getEntityListFilterInteractor: GetEntityListFilterInteractor,
        getEntityListSortInteractor: GetEntityListSortInteractor,
        addEntityRelationInteractor: AddEntityRelationInteractor,
        removeEntityRelationInteractor: RemoveEntityRelationInteractor,
        entityListArgs: EntityListFragment.Args
    ): EntityListViewModelFactory {
        return EntityListViewModelFactory(
            getEntityListInteractor,
            setEntityListFilterInteractor,
            setEntityListSortInteractor,
            getEntityListFilterInteractor,
            getEntityListSortInteractor,
            removeEntityRelationInteractor,
            addEntityRelationInteractor,
            entityListArgs
        )
    }
}
