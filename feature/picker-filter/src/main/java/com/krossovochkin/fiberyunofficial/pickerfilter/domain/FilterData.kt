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

package com.krossovochkin.fiberyunofficial.pickerfilter.domain

import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.domain.FieldData

enum class FilterMergeType(
    val value: String,
) {
    ALL("and"),
    ANY("or"),
}

enum class FilterCondition(
    val displayText: String,
    val value: String
) {
    EQUALS(displayText = "=", value = "="),
    NOT_EQUALS(displayText = "≠", value = "!=")
}

interface FilterItemData

object EmptyFilterItemData : FilterItemData

data class SingleSelectFilterItemData(
    val field: FiberyFieldSchema,
    val condition: FilterCondition?,
    val items: List<FieldData.EnumItemData>,
    val selectedItem: FieldData.EnumItemData?
) : FilterItemData
