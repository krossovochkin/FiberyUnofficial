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

package com.krossovochkin.fiberyunofficial.pickerfilter.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiRepository
import by.krossovochkin.fiberyunofficial.core.presentation.ResProvider
import dagger.Module
import dagger.Provides

@Module
object PickerFilterPresentationModule {

    @JvmStatic
    @Provides
    fun pickerFilterArgs(
        fragment: Fragment,
        pickerFilterArgsProvider: PickerFilterFragment.ArgsProvider
    ): PickerFilterFragment.Args {
        return pickerFilterArgsProvider.getPickerFilterArgs(fragment.requireArguments())
    }

    @JvmStatic
    @Provides
    fun pickerFilterViewModel(
        fragment: Fragment,
        pickerFilterViewModelFactory: PickerFilterViewModelFactory
    ): PickerFilterViewModel {
        return ViewModelProvider(fragment, pickerFilterViewModelFactory).get()
    }

    @JvmStatic
    @Provides
    fun pickerFilterViewModelFactory(
        pickerFilterArgs: PickerFilterFragment.Args,
        fiberyApiRepository: FiberyApiRepository,
        resProvider: ResProvider
    ): PickerFilterViewModelFactory {
        return PickerFilterViewModelFactory(
            pickerFilterArgs = pickerFilterArgs,
            fiberyApiRepository = fiberyApiRepository,
            resProvider = resProvider
        )
    }
}