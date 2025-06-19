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
package com.krossovochkin.fiberyunofficial.di.entitycreate

import com.krossovochkin.fiberyunofficial.GlobalDependencies
import com.krossovochkin.fiberyunofficial.entitycreate.presentation.EntityCreateFragment
import com.krossovochkin.fiberyunofficial.entitycreate.presentation.EntityCreateViewModel
import dagger.Component
import javax.inject.Scope

interface EntityCreateParentComponent : GlobalDependencies

@EntityCreate
@Component(
    dependencies = [EntityCreateParentComponent::class]
)
interface EntityCreateComponent {

    fun viewModelFactory(): EntityCreateViewModel.Factory

    fun inject(fragment: EntityCreateFragment)

    @Component.Factory
    interface Factory {

        fun create(
            entityCreateParentComponent: EntityCreateParentComponent
        ): EntityCreateComponent
    }
}

@Scope
@Retention
annotation class EntityCreate
