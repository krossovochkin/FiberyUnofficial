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

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData

sealed class EntityDetailsNavEvent {

    object BackEvent : EntityDetailsNavEvent()

    data class OnEntitySelectedEvent(
        val entity: FiberyEntityData
    ) : EntityDetailsNavEvent()

    data class OnEntityFieldEditEvent(
        val fieldSchema: FiberyFieldSchema,
        val currentEntity: FiberyEntityData?
    ) : EntityDetailsNavEvent()

    data class OnEntityTypeSelectedEvent(
        val entityTypeSchema: FiberyEntityTypeSchema,
        val entity: FiberyEntityData,
        val fieldSchema: FiberyFieldSchema
    ) : EntityDetailsNavEvent()

    data class OnSingleSelectSelectedEvent(
        val fieldSchema: FiberyFieldSchema,
        val singleSelectItem: FieldData.SingleSelectFieldData
    ) : EntityDetailsNavEvent()

    data class OpenUrlEvent(
        val url: String
    ) : EntityDetailsNavEvent()

    data class SendEmailEvent(
        val email: String
    ) : EntityDetailsNavEvent()
}
