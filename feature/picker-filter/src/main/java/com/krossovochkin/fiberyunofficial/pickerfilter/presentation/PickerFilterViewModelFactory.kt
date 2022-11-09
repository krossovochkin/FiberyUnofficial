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

package com.krossovochkin.fiberyunofficial.pickerfilter.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krossovochkin.fiberyunofficial.api.FiberyApiRepository

class PickerFilterViewModelFactory(
    private val pickerFilterArgs: PickerFilterFragment.Args,
    private val fiberyApiRepository: FiberyApiRepository,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == PickerFilterViewModel::class.java)
        return PickerFilterViewModelImpl(
            pickerFilterArgs = pickerFilterArgs,
            fiberyApiRepository = fiberyApiRepository,
        ) as T
    }
}
