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

package com.krossovochkin.fiberyunofficial.di.pickerfilter

import com.krossovochkin.fiberyunofficial.GlobalDependencies
import com.krossovochkin.fiberyunofficial.pickerfilter.presentation.PickerFilterFragment
import com.krossovochkin.fiberyunofficial.pickerfilter.presentation.PickerFilterViewModelFactory
import dagger.BindsInstance
import dagger.Component
import dagger.Lazy
import javax.inject.Scope

interface PickerFilterParentComponent : GlobalDependencies {

    fun serializer(): com.krossovochkin.serialization.Serializer
}

@PickerFilter
@Component(
    dependencies = [
        PickerFilterParentComponent::class
    ]
)
interface PickerFilterComponent {

    fun viewModelFactoryProducer(): Lazy<PickerFilterViewModelFactory>

    @Component.Factory
    interface Factory {

        fun create(
            pickerFilterParentComponent: PickerFilterParentComponent,
            @BindsInstance argsProvider: PickerFilterFragment.ArgsProvider
        ): PickerFilterComponent
    }
}

@Scope
@Retention
annotation class PickerFilter
