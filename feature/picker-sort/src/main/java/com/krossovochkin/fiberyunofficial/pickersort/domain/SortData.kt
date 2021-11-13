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
import com.krossovochkin.fiberyunofficial.domain.FiberyEntitySortData
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.pickersort.R

enum class SortCondition(
    @StringRes
    val displayNameResId: Int,
    val value: FiberyEntitySortData.Item.Condition
) {
    ASCENDING(
        displayNameResId = R.string.picker_sort_ascending,
        value = FiberyEntitySortData.Item.Condition.ASCENDING
    ),
    DESCENDING(
        displayNameResId = R.string.picker_sort_descending,
        value = FiberyEntitySortData.Item.Condition.DESCENDING
    )

    ;

    companion object {
        fun fromCondition(condition: FiberyEntitySortData.Item.Condition): SortCondition {
            return when (condition) {
                FiberyEntitySortData.Item.Condition.ASCENDING -> ASCENDING
                FiberyEntitySortData.Item.Condition.DESCENDING -> DESCENDING
            }
        }
    }
}

interface SortItemData

object EmptySortItemData : SortItemData

data class SelectedSortItemData(
    val field: FiberyFieldSchema,
    val condition: SortCondition?,
) : SortItemData
