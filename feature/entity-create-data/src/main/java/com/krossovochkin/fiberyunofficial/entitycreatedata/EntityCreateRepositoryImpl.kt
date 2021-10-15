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
package com.krossovochkin.fiberyunofficial.entitycreatedata

import com.krossovochkin.fiberyunofficial.api.FiberyApiConstants
import com.krossovochkin.fiberyunofficial.api.FiberyServiceApi
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommand
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommandArgsDto
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommandBody
import com.krossovochkin.fiberyunofficial.api.dto.checkResultSuccess
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.entitycreatedomain.EntityCreateRepository
import java.util.UUID

class EntityCreateRepositoryImpl(
    private val fiberyServiceApi: FiberyServiceApi
) : EntityCreateRepository {

    override suspend fun createEntity(
        entityTypeSchema: FiberyEntityTypeSchema,
        name: String
    ): FiberyEntityData {
        if (name.isEmpty()) {
            throw IllegalArgumentException("name should not be null")
        }

        val titleFieldName = requireNotNull(
            entityTypeSchema.fields.find { it.meta.isUiTitle }
        ) { "title is missing" }.name
        val id = UUID.randomUUID().toString()

        fiberyServiceApi
            .sendCommand(
                body = listOf(
                    FiberyCommandBody(
                        command = FiberyCommand.QUERY_CREATE.value,
                        args = FiberyCommandArgsDto(
                            type = entityTypeSchema.name,
                            entity = mapOf(
                                titleFieldName to name,
                                FiberyApiConstants.Field.ID.value to id
                            )
                        )
                    )
                )
            )
            .checkResultSuccess()

        return FiberyEntityData(
            id = id,
            publicId = "",
            title = name,
            schema = entityTypeSchema
        )
    }
}
