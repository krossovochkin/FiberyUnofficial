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
package by.krossovochkin.fiberyunofficial.entitydetails.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData

interface UpdateMultiSelectFieldInteractor {

    suspend fun execute(
        entityData: FiberyEntityData,
        fieldSchema: FiberyFieldSchema,
        addedItems: List<FieldData.EnumItemData>,
        removedItems: List<FieldData.EnumItemData>
    )
}

class UpdateMultiSelectFieldInteractorImpl(
    private val entityDetailsRepository: EntityDetailsRepository
) : UpdateMultiSelectFieldInteractor {

    override suspend fun execute(
        entityData: FiberyEntityData,
        fieldSchema: FiberyFieldSchema,
        addedItems: List<FieldData.EnumItemData>,
        removedItems: List<FieldData.EnumItemData>
    ) {
        return entityDetailsRepository.updateMultiSelectField(
            entityData = entityData,
            fieldSchema = fieldSchema,
            addedItems = addedItems,
            removedItems = removedItems
        )
    }
}
