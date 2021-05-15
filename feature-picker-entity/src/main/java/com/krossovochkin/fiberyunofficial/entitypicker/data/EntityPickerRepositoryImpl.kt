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
package com.krossovochkin.fiberyunofficial.entitypicker.data

import com.krossovochkin.fiberyunofficial.core.data.api.FiberyApiConstants
import com.krossovochkin.fiberyunofficial.core.data.api.FiberyApiRepository
import com.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import com.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommand
import com.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommandArgsDto
import com.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommandArgsQueryDto
import com.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommandBody
import com.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.core.domain.ParentEntityData
import com.krossovochkin.fiberyunofficial.entitypicker.domain.EntityPickerRepository

private const val WHERE_1 = "\$where1"

class EntityPickerRepositoryImpl(
    private val fiberyServiceApi: FiberyServiceApi,
    private val fiberyApiRepository: FiberyApiRepository
) : EntityPickerRepository {

    override suspend fun getEntityList(
        parentEntityData: ParentEntityData,
        offset: Int,
        pageSize: Int,
        searchQuery: String
    ): List<FiberyEntityData> {
        val entityType = fiberyApiRepository.getTypeSchema(parentEntityData.fieldSchema.type)
        val uiTitleType = entityType.getUiTitle()
        val idType = FiberyApiConstants.Field.ID.value
        val publicIdType = FiberyApiConstants.Field.PUBLIC_ID.value

        val dto = fiberyServiceApi.getEntities(
            listOf(
                FiberyCommandBody(
                    command = FiberyCommand.QUERY_ENTITY.value,
                    args = FiberyCommandArgsDto(
                        FiberyCommandArgsQueryDto(
                            from = entityType.name,
                            select = listOf(
                                uiTitleType,
                                idType,
                                publicIdType
                            ),
                            where = listOf(
                                FiberyApiConstants.Operator.CONTAINS.value,
                                listOf(uiTitleType),
                                WHERE_1
                            ),
                            offset = offset,
                            limit = pageSize
                        ),
                        params = mapOf(WHERE_1 to searchQuery)
                    )
                )
            )
        ).first()

        return dto.result.map {
            val title = requireNotNull(it[uiTitleType]) { "title is missing" } as String
            val id = requireNotNull(it[idType]) { "id is missing" } as String
            val publicId = requireNotNull(it[publicIdType]) { "publicId is missing" } as String
            FiberyEntityData(
                id = id,
                publicId = publicId,
                title = title,
                schema = entityType
            )
        }
    }

    private fun FiberyEntityTypeSchema.getUiTitle(): String {
        return requireNotNull(
            this.fields.find { it.meta.isUiTitle }
        ) { "title field name is missing: $this" }.name
    }
}
