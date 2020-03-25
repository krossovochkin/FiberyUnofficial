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
package by.krossovochkin.fiberyunofficial.pickersingleselect

import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import by.krossovochkin.fiberyunofficial.pickersingleselect.presentation.PickerSingleSelectDialogFragment
import by.krossovochkin.fiberyunofficial.pickersingleselect.presentation.PickerSingleSelectPresentationModule
import by.krossovochkin.fiberyunofficial.pickersingleselect.presentation.PickerSingleSelectViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

interface PickerSingleSelectParentComponent : GlobalDependencies {

    fun pickerSingleSelectArgsProvider(): PickerSingleSelectDialogFragment.ArgsProvider
}

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

    fun pickerSingleSelectViewModel(): PickerSingleSelectViewModel

    fun inject(fragment: PickerSingleSelectDialogFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun fragment(fragment: Fragment): Builder

        fun pickerSingleSelectParentComponent(
            pickerSingleSelectParentComponent: PickerSingleSelectParentComponent
        ): Builder

        fun build(): PickerSingleSelectComponent
    }
}

@Scope
@Retention
annotation class PickerSingleSelect
