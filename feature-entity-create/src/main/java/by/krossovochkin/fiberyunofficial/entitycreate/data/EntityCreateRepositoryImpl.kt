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
package by.krossovochkin.fiberyunofficial.entitycreate.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiConstants
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiRepository
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommand
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCreateCommandArgsDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCreateCommandBody
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.entitycreate.domain.EntityCreateRepository

class EntityCreateRepositoryImpl(
    private val fiberyServiceApi: FiberyServiceApi,
    private val fiberyApiRepository: FiberyApiRepository
) : EntityCreateRepository {

    override suspend fun createEntity(
        entityTypeSchema: FiberyEntityTypeSchema,
        name: String,
        entityParams: Pair<FiberyFieldSchema, FiberyEntityData>?
    ) {
        if (name.isEmpty()) {
            throw IllegalArgumentException("name should not be null")
        }
        val titleFieldName = entityTypeSchema.fields
            .find { it.meta.isUiTitle }!!.name
        val fields = mutableMapOf<String, Any>().apply {
            this[titleFieldName] = name

            entityParams?.let {
                val relationFieldName = fiberyApiRepository.getTypeSchema(entityTypeSchema.name)
                    .fields.find { field -> field.meta.relationId == it.first.meta.relationId }!!
                    .name
                this[relationFieldName] = mapOf(
                    FiberyApiConstants.Field.ID.value to it.second.id
                )
            }
        }
        fiberyServiceApi.createEntity(
            listOf(
                FiberyCreateCommandBody(
                    command = FiberyCommand.QUERY_CREATE.value,
                    args = FiberyCreateCommandArgsDto(
                        type = entityTypeSchema.name,
                        entity = fields
                    )
                )
            )
        )
    }
}
