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

package com.krossovochkin.fiberyunofficial.pickersort.domain

import androidx.annotation.StringRes
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.pickersort.R

enum class SortCondition(
    @StringRes
    val displayNameResId: Int,
    val value: String
) {
    ASCENDING(displayNameResId = R.string.picker_sort_ascending, value = "q/asc"),
    DESCENDING(displayNameResId = R.string.picker_sort_descending, value = "q/desc")
}

interface SortItemData

object EmptySortItemData : SortItemData

data class SelectedSortItemData(
    val field: FiberyFieldSchema,
    val condition: SortCondition?,
) : SortItemData
