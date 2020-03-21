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
package by.krossovochkin.fiberyunofficial.entitydetails

import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import by.krossovochkin.fiberyunofficial.entitydetails.data.EntityDetailsDataModule
import by.krossovochkin.fiberyunofficial.entitydetails.domain.EntityDetailsDomainModule
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragment
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsPresentationModule
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

interface EntityDetailsParentComponent : GlobalDependencies {

    fun entityDetailsArgsProvider(): EntityDetailsFragment.ArgsProvider
}

@EntityDetails
@Component(
    modules = [
        EntityDetailsDataModule::class,
        EntityDetailsDomainModule::class,
        EntityDetailsPresentationModule::class
    ],
    dependencies = [
        EntityDetailsParentComponent::class
    ]
)
interface EntityDetailsComponent {

    fun entityDetailsViewModel(): EntityDetailsViewModel

    fun inject(fragment: EntityDetailsFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun fragment(fragment: Fragment): Builder

        fun entityDetailsParentComponent(entityDetailsParentComponent: EntityDetailsParentComponent): Builder

        fun build(): EntityDetailsComponent
    }
}

@Scope
@Retention
annotation class EntityDetails
