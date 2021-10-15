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
package com.krossovochkin.fiberyunofficial.entitydetails.presentation

import android.os.Parcelable
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.domain.FieldData
import kotlinx.parcelize.Parcelize

const val RESULT_KEY_MULTI_SELECT_PICKED = "multi_select_picked"

@Parcelize
data class MultiSelectPickedData(
    val fieldSchema: FiberyFieldSchema,
    val addedItems: List<FieldData.EnumItemData>,
    val removedItems: List<FieldData.EnumItemData>
) : Parcelable
