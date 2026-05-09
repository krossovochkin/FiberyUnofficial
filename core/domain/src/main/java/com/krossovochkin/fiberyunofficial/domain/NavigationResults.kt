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
package com.krossovochkin.fiberyunofficial.domain

data class PickerFilterResultData(
    val entityType: FiberyEntityTypeSchema,
    val filter: FiberyEntityFilterData
)

data class PickerSortResultData(
    val entityType: FiberyEntityTypeSchema,
    val sort: FiberyEntitySortData
)

data class PickerSingleSelectResultData(
    val fieldSchema: FiberyFieldSchema,
    val selectedValue: FieldData.EnumItemData?
)

data class MultiSelectPickedData(
    val fieldSchema: FiberyFieldSchema,
    val addedItems: List<FieldData.EnumItemData>,
    val removedItems: List<FieldData.EnumItemData>
)

data class PickerEntityResultData(
    val fieldSchema: FiberyFieldSchema,
    val entity: FiberyEntityData?
)
