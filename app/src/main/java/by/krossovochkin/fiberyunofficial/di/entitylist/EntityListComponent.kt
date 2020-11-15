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

import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import by.krossovochkin.fiberyunofficial.core.data.serialization.Serializer
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragment
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListViewModelFactory
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

interface EntityListParentComponent : GlobalDependencies {

    fun serializer(): Serializer
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

    fun viewModelFactoryProducer(): () -> EntityListViewModelFactory

    @Component.Factory
    interface Factory {

        fun create(
            entityListParentComponent: EntityListParentComponent,
            @BindsInstance argsProvider: EntityListFragment.ArgsProvider
        ): EntityListComponent
    }
}

@Scope
@Retention
annotation class EntityList
