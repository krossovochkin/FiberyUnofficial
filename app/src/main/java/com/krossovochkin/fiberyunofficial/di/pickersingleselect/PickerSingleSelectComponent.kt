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
package com.krossovochkin.fiberyunofficial.di.pickersingleselect

import com.krossovochkin.fiberyunofficial.GlobalDependencies
import com.krossovochkin.fiberyunofficial.pickersingleselect.presentation.PickerSingleSelectDialogFragment
import com.krossovochkin.fiberyunofficial.pickersingleselect.presentation.PickerSingleSelectViewModelFactory
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

interface PickerSingleSelectParentComponent : GlobalDependencies

@PickerSingleSelect
@Component(
    modules = [
        PickerSingleSelectPresentationModule::class
    ],
    dependencies = [
        PickerSingleSelectParentComponent::class
    ]
)
interface PickerSingleSelectComponent {

    fun viewModelFactoryProducer(): () -> PickerSingleSelectViewModelFactory

    @Component.Factory
    interface Factory {

        fun create(
            pickerSingleSelectParentComponent: PickerSingleSelectParentComponent,
            @BindsInstance argsProvider: PickerSingleSelectDialogFragment.ArgsProvider
        ): PickerSingleSelectComponent
    }
}

@Scope
@Retention
annotation class PickerSingleSelect
