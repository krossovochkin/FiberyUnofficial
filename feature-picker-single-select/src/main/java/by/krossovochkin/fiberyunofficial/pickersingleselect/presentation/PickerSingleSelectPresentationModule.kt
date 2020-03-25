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
package by.krossovochkin.fiberyunofficial.pickersingleselect.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import dagger.Module
import dagger.Provides

@Module
object PickerSingleSelectPresentationModule {

    @JvmStatic
    @Provides
    fun pickerSingleSelectArgs(
        fragment: Fragment,
        pickerSingleSelectArgsProvider: PickerSingleSelectDialogFragment.ArgsProvider
    ): PickerSingleSelectDialogFragment.Args {
        return pickerSingleSelectArgsProvider.getPickerSingleSelectArgs(fragment.requireArguments())
    }

    @JvmStatic
    @Provides
    fun pickerSingleSelectViewModel(
        fragment: Fragment,
        pickerSingleSelectViewModelFactory: PickerSingleSelectViewModelFactory
    ): PickerSingleSelectViewModel {
        return ViewModelProvider(fragment, pickerSingleSelectViewModelFactory).get()
    }

    @JvmStatic
    @Provides
    fun pickerSingleSelectViewModelFactory(
        args: PickerSingleSelectDialogFragment.Args
    ): PickerSingleSelectViewModelFactory {
        return PickerSingleSelectViewModelFactory(
            args
        )
    }
}
