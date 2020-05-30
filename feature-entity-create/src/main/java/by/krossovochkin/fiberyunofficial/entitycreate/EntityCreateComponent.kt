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
package by.krossovochkin.fiberyunofficial.entitycreate

import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import by.krossovochkin.fiberyunofficial.entitycreate.data.EntityCreateDataModule
import by.krossovochkin.fiberyunofficial.entitycreate.domain.EntityCreateDomainModule
import by.krossovochkin.fiberyunofficial.entitycreate.presentation.EntityCreateFragment
import by.krossovochkin.fiberyunofficial.entitycreate.presentation.EntityCreatePresentationModule
import by.krossovochkin.fiberyunofficial.entitycreate.presentation.EntityCreateViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

interface EntityCreateParentComponent : GlobalDependencies {

    fun entityCreateArgsProvider(): EntityCreateFragment.ArgsProvider
}

@EntityCreate
@Component(
    modules = [
        EntityCreateDataModule::class,
        EntityCreateDomainModule::class,
        EntityCreatePresentationModule::class
    ],
    dependencies = [EntityCreateParentComponent::class]
)
interface EntityCreateComponent {

    fun entityCreateViewModel(): EntityCreateViewModel

    fun inject(fragment: EntityCreateFragment)

    @Component.Factory
    interface Factory {

        fun create(
            entityCreateParentComponent: EntityCreateParentComponent,
            @BindsInstance fragment: Fragment
        ): EntityCreateComponent
    }
}

@Scope
@Retention
annotation class EntityCreate
