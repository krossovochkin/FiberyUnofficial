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
package by.krossovochkin.fiberyunofficial.entitylist

import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import by.krossovochkin.fiberyunofficial.entitylist.data.EntityListDataModule
import by.krossovochkin.fiberyunofficial.entitylist.domain.EntityListDomainModule
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragment
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListPresentationModule
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

interface EntityListParentComponent : GlobalDependencies {

    fun entityListArgsProvider(): EntityListFragment.ArgsProvider
}

@EntityList
@Component(
    modules = [
        EntityListDataModule::class,
        EntityListDomainModule::class,
        EntityListPresentationModule::class
    ],
    dependencies = [
        EntityListParentComponent::class
    ]
)
interface EntityListComponent {

    fun entityListViewModel(): EntityListViewModel

    fun inject(fragment: EntityListFragment)

    @Component.Factory
    interface Factory {

        fun create(
            entityListParentComponent: EntityListParentComponent,
            @BindsInstance fragment: Fragment
        ): EntityListComponent
    }
}

@Scope
@Retention
annotation class EntityList
