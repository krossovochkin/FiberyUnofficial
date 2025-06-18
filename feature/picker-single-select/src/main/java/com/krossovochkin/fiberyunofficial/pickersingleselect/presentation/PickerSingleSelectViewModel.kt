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
package com.krossovochkin.fiberyunofficial.pickersingleselect.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.domain.FieldData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class PickerSingleSelectViewModel @AssistedInject constructor(
    @Assisted private val args: PickerSingleSelectDialogFragment.Args
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(args: PickerSingleSelectDialogFragment.Args): PickerSingleSelectViewModel
    }

    companion object {
        fun provideFactory(
            factory: Factory,
            args: PickerSingleSelectDialogFragment.Args
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return factory.create(args) as T
            }
        }
    }

    val item: FieldData.SingleSelectFieldData
        get() = args.item

    val fieldSchema: FiberyFieldSchema
        get() = args.parentEntityData.fieldSchema
}
