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
package by.krossovochkin.fiberyunofficial.di.entitylist

import by.krossovochkin.fiberyunofficial.core.presentation.ResProvider
import by.krossovochkin.fiberyunofficial.entitylist.domain.AddEntityRelationInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListFilterInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListSortInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.RemoveEntityRelationInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListFilterInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListSortInteractor
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragment
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListViewModelFactory
import dagger.Module
import dagger.Provides

@Module
object EntityListPresentationModule {

    @Suppress("LongParameterList")
    @JvmStatic
    @Provides
    fun entityListViewModelFactoryProvider(
        getEntityListInteractor: GetEntityListInteractor,
        setEntityListFilterInteractor: SetEntityListFilterInteractor,
        setEntityListSortInteractor: SetEntityListSortInteractor,
        getEntityListFilterInteractor: GetEntityListFilterInteractor,
        getEntityListSortInteractor: GetEntityListSortInteractor,
        addEntityRelationInteractor: AddEntityRelationInteractor,
        removeEntityRelationInteractor: RemoveEntityRelationInteractor,
        resProvider: ResProvider,
        argsProvider: EntityListFragment.ArgsProvider
    ): () -> EntityListViewModelFactory {
        return {
            EntityListViewModelFactory(
                getEntityListInteractor,
                setEntityListFilterInteractor,
                setEntityListSortInteractor,
                getEntityListFilterInteractor,
                getEntityListSortInteractor,
                removeEntityRelationInteractor,
                addEntityRelationInteractor,
                resProvider,
                argsProvider.getEntityListArgs()
            )
        }
    }
}
