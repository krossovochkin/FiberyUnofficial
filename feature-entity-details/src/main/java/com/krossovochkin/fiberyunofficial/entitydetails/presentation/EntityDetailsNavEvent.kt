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

import android.view.View
import com.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.core.domain.FieldData
import com.krossovochkin.fiberyunofficial.core.domain.ParentEntityData

sealed class EntityDetailsNavEvent {

    object BackEvent : EntityDetailsNavEvent()

    data class OnEntitySelectedEvent(
        val entity: FiberyEntityData,
        val itemView: View
    ) : EntityDetailsNavEvent()

    data class OnEntityFieldEditEvent(
        val parentEntityData: ParentEntityData,
        val currentEntity: FiberyEntityData?,
        val itemView: View
    ) : EntityDetailsNavEvent()

    data class OnEntityTypeSelectedEvent(
        val parentEntityData: ParentEntityData,
        val entityTypeSchema: FiberyEntityTypeSchema,
        val itemView: View
    ) : EntityDetailsNavEvent()

    data class OnSingleSelectSelectedEvent(
        val parentEntityData: ParentEntityData,
        val singleSelectItem: FieldData.SingleSelectFieldData
    ) : EntityDetailsNavEvent()

    data class OnMultiSelectSelectedEvent(
        val parentEntityData: ParentEntityData,
        val multiSelectItem: FieldData.MultiSelectFieldData
    ) : EntityDetailsNavEvent()

    data class OpenUrlEvent(
        val url: String
    ) : EntityDetailsNavEvent()

    data class SendEmailEvent(
        val email: String
    ) : EntityDetailsNavEvent()
}
