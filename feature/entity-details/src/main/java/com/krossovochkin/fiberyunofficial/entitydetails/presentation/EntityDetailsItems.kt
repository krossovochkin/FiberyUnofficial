/*
   Copyright 2020 Vasya Drobushkov

   Licensed under the Apache License, Version 2.0 (the \"License\");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an \"AS IS\" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package com.krossovochkin.fiberyunofficial.entitydetails.presentation

import com.krossovochkin.fiberyunofficial.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.domain.FieldData
import com.krossovochkin.fiberyunofficial.ui.list.ListItem

data class FieldHeaderItem(
    val title: String
) : ListItem

data class FieldTextItem(
    val title: String,
    val text: String
) : ListItem

data class FieldUrlItem(
    val title: String,
    val url: String
) : ListItem {

    val isOpenAvailable: Boolean = url.isNotEmpty()
}

data class FieldEmailItem(
    val title: String,
    val email: String
) : ListItem {

    val isOpenAvailable: Boolean = email.isNotEmpty()
}

data class FieldSingleSelectItem(
    val title: String,
    val text: String,
    val values: List<FieldData.EnumItemData>,
    val fieldSchema: FiberyFieldSchema,
    val singleSelectData: FieldData.SingleSelectFieldData
) : ListItem

data class FieldMultiSelectItem(
    val title: String,
    val text: String,
    val values: List<FieldData.EnumItemData>,
    val fieldSchema: FiberyFieldSchema,
    val multiSelectData: FieldData.MultiSelectFieldData
) : ListItem

data class FieldRichTextItem(
    val title: String,
    val value: String
) : ListItem

data class FieldRelationItem(
    val title: String,
    val entityName: String,
    val entityData: FiberyEntityData?,
    val fieldSchema: FiberyFieldSchema
) : ListItem {

    val isDeleteAvailable: Boolean = entityData != null

    val isOpenAvailable: Boolean = entityData != null
}

data class FieldCollectionItem(
    val title: String,
    val countText: String,
    val entityTypeSchema: FiberyEntityTypeSchema,
    val fieldSchema: FiberyFieldSchema
) : ListItem

data class FieldCheckboxItem(
    val title: String,
    val value: Boolean
) : ListItem
