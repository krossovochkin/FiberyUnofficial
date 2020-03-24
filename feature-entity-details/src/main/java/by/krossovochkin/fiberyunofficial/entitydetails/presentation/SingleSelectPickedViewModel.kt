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

class SingleSelectPickedViewModel : ViewModel() {

    private val mutablePickedSingleSelect =
        MutableLiveData<Event<Pair<FiberyFieldSchema, FieldData.EnumItemData?>>>()
    val pickedSingleSelect: LiveData<Event<Pair<FiberyFieldSchema, FieldData.EnumItemData?>>> =
        mutablePickedSingleSelect

    fun pickSingleSelect(fieldSchema: FiberyFieldSchema, enumItemData: FieldData.EnumItemData?) {
        mutablePickedSingleSelect.value = Event(fieldSchema to enumItemData)
    }
}