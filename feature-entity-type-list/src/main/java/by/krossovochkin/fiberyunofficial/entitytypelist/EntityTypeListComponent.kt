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
package by.krossovochkin.fiberyunofficial.entitytypelist

import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import by.krossovochkin.fiberyunofficial.entitytypelist.data.EntityTypeListDataModule
import by.krossovochkin.fiberyunofficial.entitytypelist.domain.EntityTypeListDomainModule
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragment
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListPresentationModule
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

interface EntityTypeListParentComponent : GlobalDependencies {

    fun entityTypeListArgsProvider(): EntityTypeListFragment.ArgsProvider
}

@EntityTypeList
@Component(
    modules = [
        EntityTypeListDataModule::class,
        EntityTypeListDomainModule::class,
        EntityTypeListPresentationModule::class
    ],
    dependencies = [
        EntityTypeListParentComponent::class
    ]
)
interface EntityTypeListComponent {

    fun entityTypeListViewModel(): EntityTypeListViewModel

    fun inject(fragment: EntityTypeListFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun fragment(fragment: Fragment): Builder

        fun entityTypeListGlobalDependencies(
            entityTypeListParentComponent: EntityTypeListParentComponent
        ): Builder

        fun build(): EntityTypeListComponent
    }
}

@Scope
@Retention
annotation class EntityTypeList
