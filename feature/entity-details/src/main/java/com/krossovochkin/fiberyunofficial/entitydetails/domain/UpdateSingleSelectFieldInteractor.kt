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
package com.krossovochkin.fiberyunofficial.entitydetails.domain

import com.krossovochkin.fiberyunofficial.api.FiberyApiConstants
import com.krossovochkin.fiberyunofficial.api.FiberyServiceApi
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommand
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommandArgsDto
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommandBody
import com.krossovochkin.fiberyunofficial.api.dto.checkResultSuccess
import com.krossovochkin.fiberyunofficial.domain.FieldData
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData
import javax.inject.Inject

class UpdateSingleSelectFieldInteractor @Inject constructor(
    private val fiberyServiceApi: FiberyServiceApi,
) {

    suspend fun execute(
        parentEntityData: ParentEntityData,
        singleSelectItem: FieldData.EnumItemData
    ) {
        fiberyServiceApi
            .sendCommand(
                body = listOf(
                    FiberyCommandBody(
                        command = FiberyCommand.QUERY_UPDATE.value,
                        args = FiberyCommandArgsDto(
                            type = parentEntityData.parentEntity.schema.name,
                            entity = mapOf(
                                FiberyApiConstants.Field.ID.value to parentEntityData.parentEntity.id,
                                parentEntityData.fieldSchema.name to mapOf(
                                    FiberyApiConstants.Field.ID.value to singleSelectItem.id
                                )
                            ),
                            field = parentEntityData.fieldSchema.name
                        )
                    )
                )
            )
            .checkResultSuccess()
    }
}
