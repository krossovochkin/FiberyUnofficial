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
package com.krossovochkin.fiberyunofficial.entitylist.domain

import com.krossovochkin.fiberyunofficial.api.FiberyApiConstants
import com.krossovochkin.fiberyunofficial.api.FiberyApiRepository
import com.krossovochkin.fiberyunofficial.api.FiberyServiceApi
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommand
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommandArgsDto
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommandArgsQueryDto
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommandBody
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityFilterData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntitySortData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData
import com.krossovochkin.fiberyunofficial.entitylist.data.EntityListFiltersSortStorage
import javax.inject.Inject

private const val PARAM_ID = "\$id"

class GetEntityListInteractor @Inject constructor(
    private val fiberyServiceApi: FiberyServiceApi,
    private val fiberyApiRepository: FiberyApiRepository,
    private val entityListFiltersSortStorage: EntityListFiltersSortStorage,
) {

    suspend fun execute(
        entityType: FiberyEntityTypeSchema,
        offset: Int,
        pageSize: Int,
        parentEntityData: ParentEntityData?
    ): List<FiberyEntityData> {
        return if (parentEntityData != null) {
            getInnerEntityList(parentEntityData, offset, pageSize)
        } else {
            getRootEntityList(entityType, offset, pageSize)
        }
    }

    private suspend fun getRootEntityList(
        entityType: FiberyEntityTypeSchema,
        offset: Int,
        pageSize: Int,
    ): List<FiberyEntityData> {
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
                            where = entityListFiltersSortStorage.getFilter(entityType.name)
                                ?.toWhereClause(),
                            orderBy = entityListFiltersSortStorage.getSort(entityType.name)
                                ?.toOrderBy(),
                            offset = offset,
                            limit = pageSize
                        ),
                        params = entityListFiltersSortStorage.getFilter(entityType.name)
                            ?.toParams()
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

    private suspend fun getInnerEntityList(
        parentEntityData: ParentEntityData,
        offset: Int,
        pageSize: Int
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
                        query = FiberyCommandArgsQueryDto(
                            from = parentEntityData.parentEntity.schema.name,
                            select = mapOf(
                                parentEntityData.fieldSchema.name to FiberyCommandArgsQueryDto(
                                    from = parentEntityData.fieldSchema.name,
                                    select = listOf(
                                        uiTitleType,
                                        idType,
                                        publicIdType
                                    ),
                                    offset = offset,
                                    limit = pageSize
                                )
                            ),
                            where = listOf(
                                FiberyApiConstants.Operator.EQUALS.value,
                                listOf(FiberyApiConstants.Field.ID.value),
                                PARAM_ID
                            ),
                            limit = 1
                        ),
                        params = mapOf(PARAM_ID to parentEntityData.parentEntity.id)
                    )
                )
            )
        ).first()

        @Suppress("UNCHECKED_CAST")
        val result = dto.result as List<Map<String, List<Map<String, Any>>>>
        return result.first()
            .flatMap { it.value }
            .map {
                val map = it
                val title = requireNotNull(map[uiTitleType]) { "title is missing" } as String
                val id = requireNotNull(map[idType]) { "id is missing" } as String
                val publicId = requireNotNull(map[publicIdType]) { "publicId is missing" } as String
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

    private fun FiberyEntitySortData.toOrderBy(): List<Any>? {
        if (this.items.isEmpty()) {
            return null
        }

        return this.items.map { data -> data.toOrderBy() }
    }

    private fun FiberyEntitySortData.Item.toOrderBy(): List<Any> {
        return listOf(listOf(this.field.name), this.condition.value)
    }

    private fun FiberyEntityFilterData.toWhereClause(): List<Any>? {
        if (this.items.isEmpty()) {
            return null
        }
        if (this.items.size == 1) {
            return this.items.single().toClause(1)
        }
        val (mergeType, items) = this
        return listOf(mergeType.value) +
            items.mapIndexed { index, data -> data.toClause(index + 1) }
    }

    private fun FiberyEntityFilterData.Item.toClause(index: Int): List<Any> {
        return if (this is FiberyEntityFilterData.Item.SingleSelectItem) {
            listOf(
                this.condition.value,
                listOf(this.field.name, FiberyApiConstants.Field.ENUM_NAME.value),
                "\$where$index"
            )
        } else {
            TODO("implement")
        }
    }

    private fun FiberyEntityFilterData.toParams(): Map<String, Any>? {
        if (this.items.isEmpty()) {
            return null
        }
        return this.items.mapIndexed { index, data ->
            "\$where${index + 1}" to data.toParamValue()
        }.toMap()
    }

    private fun FiberyEntityFilterData.Item.toParamValue(): Any {
        return if (this is FiberyEntityFilterData.Item.SingleSelectItem) {
            this.param.title
        } else {
            TODO("implement")
        }
    }
}
