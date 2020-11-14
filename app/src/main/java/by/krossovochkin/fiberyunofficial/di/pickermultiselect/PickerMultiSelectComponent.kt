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
package by.krossovochkin.fiberyunofficial.di.pickermultiselect

import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import by.krossovochkin.fiberyunofficial.pickermultiselect.presentation.PickerMultiSelectDialogFragment
import by.krossovochkin.fiberyunofficial.pickermultiselect.presentation.PickerMultiSelectViewModelFactory
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

interface PickerMultiSelectParentComponent : GlobalDependencies

@PickerMultiSelect
@Component(
    modules = [
        PickerMultiSelectPresentationModule::class
    ],
    dependencies = [
        PickerMultiSelectParentComponent::class
    ]
)
interface PickerMultiSelectComponent {

    fun viewModelFactoryProducer(): () -> PickerMultiSelectViewModelFactory

    @Component.Factory
    interface Factory {

        fun create(
            pickerMultiSelectParentComponent: PickerMultiSelectParentComponent,
            @BindsInstance argsProvider: PickerMultiSelectDialogFragment.ArgsProvider
        ): PickerMultiSelectComponent
    }
}

@Scope
@Retention
annotation class PickerMultiSelect
