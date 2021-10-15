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

import com.krossovochkin.core.presentation.list.ListItem
import com.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.core.domain.FieldData
import com.krossovochkin.fiberyunofficial.pickerfilter.domain.FilterCondition

data class EmptyFilterItem(
    val fields: List<FiberyFieldSchema>
) : ListItem

data class SingleSelectFilterItem(
    val fields: List<FiberyFieldSchema>,
    val field: FiberyFieldSchema,
    val conditions: List<FilterCondition>,
    val condition: FilterCondition?,
    val values: List<FieldData.EnumItemData>,
    val selectedValue: FieldData.EnumItemData?
) : ListItem
