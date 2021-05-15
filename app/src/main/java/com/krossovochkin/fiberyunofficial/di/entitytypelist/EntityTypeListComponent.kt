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
package com.krossovochkin.fiberyunofficial.di.entitytypelist

import com.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import com.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragment
import com.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListViewModelFactory
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

interface EntityTypeListParentComponent : GlobalDependencies

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

    fun viewModelFactoryProducer(): () -> EntityTypeListViewModelFactory

    @Component.Factory
    interface Factory {

        fun create(
            entityTypeListParentComponent: EntityTypeListParentComponent,
            @BindsInstance argsProvider: EntityTypeListFragment.ArgsProvider
        ): EntityTypeListComponent
    }
}

@Scope
@Retention
annotation class EntityTypeList
