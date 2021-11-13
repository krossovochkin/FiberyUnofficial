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

import androidx.annotation.StringRes
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityFilterData
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.domain.FieldData
import com.krossovochkin.fiberyunofficial.pickerfilter.R

enum class FilterMergeType(
    @StringRes
    val displayNameResId: Int,
    val value: FiberyEntityFilterData.MergeType,
) {
    ALL(R.string.picker_filter_all, FiberyEntityFilterData.MergeType.ALL),
    ANY(R.string.picker_filter_any, FiberyEntityFilterData.MergeType.ANY)

    ;

    companion object {
        fun fromMergeType(mergeType: FiberyEntityFilterData.MergeType): FilterMergeType {
            return when (mergeType) {
                FiberyEntityFilterData.MergeType.ALL -> ALL
                FiberyEntityFilterData.MergeType.ANY -> ANY
            }
        }
    }
}

enum class FilterCondition(
    @StringRes
    val displayStringResId: Int,
    val value: FiberyEntityFilterData.Item.Condition
) {
    EQUALS(
        displayStringResId = R.string.picker_filter_equals,
        value = FiberyEntityFilterData.Item.Condition.EQUALS
    ),
    NOT_EQUALS(
        displayStringResId = R.string.picker_filter_not_equals,
        value = FiberyEntityFilterData.Item.Condition.NOT_EQUALS
    )

    ;

    companion object {
        fun fromCondition(condition: FiberyEntityFilterData.Item.Condition): FilterCondition {
            return when (condition) {
                FiberyEntityFilterData.Item.Condition.EQUALS -> EQUALS
                FiberyEntityFilterData.Item.Condition.NOT_EQUALS -> NOT_EQUALS
            }
        }
    }
}

interface FilterItemData

object EmptyFilterItemData : FilterItemData

data class SingleSelectFilterItemData(
    val field: FiberyFieldSchema,
    val condition: FilterCondition?,
    val items: List<FieldData.EnumItemData>,
    val selectedItem: FieldData.EnumItemData?
) : FilterItemData
