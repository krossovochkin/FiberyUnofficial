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
package by.krossovochkin.fiberyunofficial.entitydetails.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData
import by.krossovochkin.fiberyunofficial.core.presentation.Event

class MultiSelectPickedViewModel : ViewModel() {

    private val mutablePickedMultiSelect = MutableLiveData<Event<MultiSelectPickedData>>()
    val pickedMultiSelect: LiveData<Event<MultiSelectPickedData>> = mutablePickedMultiSelect

    fun pickMultiSelect(
        fieldSchema: FiberyFieldSchema,
        addedItems: List<FieldData.EnumItemData>,
        removedItems: List<FieldData.EnumItemData>
    ) {
        mutablePickedMultiSelect.value = Event(
            MultiSelectPickedData(
                fieldSchema = fieldSchema,
                addedItems = addedItems,
                removedItems = removedItems
            )
        )
    }

    data class MultiSelectPickedData(
        val fieldSchema: FiberyFieldSchema,
        val addedItems: List<FieldData.EnumItemData>,
        val removedItems: List<FieldData.EnumItemData>
    )
}