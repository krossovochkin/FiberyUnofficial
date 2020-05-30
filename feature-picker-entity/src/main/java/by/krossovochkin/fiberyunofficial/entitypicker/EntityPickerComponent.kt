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
package by.krossovochkin.fiberyunofficial.entitypicker

import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import by.krossovochkin.fiberyunofficial.entitypicker.data.EntityPickerDataModule
import by.krossovochkin.fiberyunofficial.entitypicker.domain.EntityPickerDomainModule
import by.krossovochkin.fiberyunofficial.entitypicker.presentation.EntityPickerFragment
import by.krossovochkin.fiberyunofficial.entitypicker.presentation.EntityPickerPresentationModule
import by.krossovochkin.fiberyunofficial.entitypicker.presentation.EntityPickerViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

interface EntityPickerParentComponent : GlobalDependencies {

    fun entityPickerArgsProvider(): EntityPickerFragment.ArgsProvider
}

@EntityPicker
@Component(
    modules = [
        EntityPickerDataModule::class,
        EntityPickerDomainModule::class,
        EntityPickerPresentationModule::class
    ],
    dependencies = [
        EntityPickerParentComponent::class
    ]
)
interface EntityPickerComponent {

    fun entityPickerViewModel(): EntityPickerViewModel

    fun inject(fragment: EntityPickerFragment)

    @Component.Factory
    interface Factory {

        fun create(
            entityPickerParentComponent: EntityPickerParentComponent,
            @BindsInstance fragment: Fragment
        ): EntityPickerComponent
    }
}

@Scope
@Retention
annotation class EntityPicker
