/*
 *
 *    Copyright 2020 Vasya Drobushkov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *
 *
 */

package com.krossovochkin.fiberyunofficial.pickerfilter

import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import com.krossovochkin.fiberyunofficial.pickerfilter.presentation.PickerFilterFragment
import com.krossovochkin.fiberyunofficial.pickerfilter.presentation.PickerFilterPresentationModule
import com.krossovochkin.fiberyunofficial.pickerfilter.presentation.PickerFilterViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

interface PickerFilterParentComponent : GlobalDependencies {

    fun pickerFilterArgsProvider(): PickerFilterFragment.ArgsProvider
}

@PickerFilter
@Component(
    modules = [
        PickerFilterPresentationModule::class
    ],
    dependencies = [
        PickerFilterParentComponent::class
    ]
)
interface PickerFilterComponent {

    fun pickerFilterViewModel(): PickerFilterViewModel

    fun inject(fragment: PickerFilterFragment)

    @Component.Factory
    interface Factory {

        fun create(
            pickerFilterParentComponent: PickerFilterParentComponent,
            @BindsInstance fragment: Fragment
        ): PickerFilterComponent
    }
}

@Scope
@Retention
annotation class PickerFilter