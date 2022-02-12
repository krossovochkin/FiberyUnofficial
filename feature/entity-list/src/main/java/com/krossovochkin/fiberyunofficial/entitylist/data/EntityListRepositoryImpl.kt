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
package com.krossovochkin.fiberyunofficial.entitylist.data

import com.krossovochkin.fiberyunofficial.api.FiberyApiConstants
import com.krossovochkin.fiberyunofficial.api.FiberyApiRepository
import com.krossovochkin.fiberyunofficial.api.FiberyServiceApi
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommand
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommandArgsDto
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommandArgsQueryDto
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommandBody
import com.krossovochkin.fiberyunofficial.api.dto.checkResultSuccess
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityFilterData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntitySortData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData
import com.krossovochkin.fiberyunofficial.entitylist.domain.EntityListRepository
import com.krossovochkin.serialization.Serializer

class EntityListRepositoryImpl(
    private val fiberyServiceApi: FiberyServiceApi,
    private val fiberyApiRepository: FiberyApiRepository,
    private val entityListFiltersSortStorage: EntityListFiltersSortStorage,
    private val serializer: Serializer,
) : EntityListRepository {

    override suspend fun getEntityList(
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
                                ?.toFilterJson()
                                ?.let { serializer.jsonToList(it, Any::class.java) },
                            orderBy = entityListFiltersSortStorage.getSort(entityType.name)
                                ?.toJson()
                                ?.let { serializer.jsonToList(it, Any::class.java) },
                            offset = offset,
                            limit = pageSize
                        ),
                        params = entityListFiltersSortStorage.getFilter(entityType.name)
                            ?.toParamsJson()
                            ?.let { serializer.jsonToMap(it, String::class.java, Any::class.java) }
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

    override fun setEntityListFilter(
        entityType: FiberyEntityTypeSchema,
        filter: FiberyEntityFilterData
    ) {
        entityListFiltersSortStorage.setFilter(entityType.name, filter)
    }

    override fun setEntityListSort(entityType: FiberyEntityTypeSchema, sort: FiberyEntitySortData) {
        entityListFiltersSortStorage.setSort(entityType.name, sort)
    }

    override fun getEntityListFilter(entityType: FiberyEntityTypeSchema): FiberyEntityFilterData {
        return entityListFiltersSortStorage.getFilter(entityType.name)
            ?: FiberyEntityFilterData(
                mergeType = FiberyEntityFilterData.MergeType.ALL,
                items = emptyList()
            )
    }

    override fun getEntityListSort(entityType: FiberyEntityTypeSchema): FiberyEntitySortData {
        return entityListFiltersSortStorage.getSort(entityType.name)
            ?: FiberyEntitySortData(items = emptyList())
    }

    override suspend fun removeRelation(
        parentEntityData: ParentEntityData,
        childEntity: FiberyEntityData
    ) {
        fiberyServiceApi
            .sendCommand(
                body = listOf(
                    FiberyCommandBody(
                        command = FiberyCommand.QUERY_REMOVE_COLLECTION_ITEM.value,
                        args = FiberyCommandArgsDto(
                            type = parentEntityData.parentEntity.schema.name,
                            field = parentEntityData.fieldSchema.name,
                            items = listOf(mapOf(FiberyApiConstants.Field.ID.value to childEntity.id)),
                            entity = mapOf(
                                FiberyApiConstants.Field.ID.value to parentEntityData.parentEntity.id
                            )
                        )
                    )
                )
            )
            .checkResultSuccess()
    }

    override suspend fun addRelation(
        parentEntityData: ParentEntityData,
        childEntity: FiberyEntityData
    ) {
        fiberyServiceApi
            .sendCommand(
                body = listOf(
                    FiberyCommandBody(
                        command = FiberyCommand.QUERY_ADD_COLLECTION_ITEM.value,
                        args = FiberyCommandArgsDto(
                            entity = mapOf(
                                FiberyApiConstants.Field.ID.value to parentEntityData.parentEntity.id
                            ),
                            field = parentEntityData.fieldSchema.name,
                            type = parentEntityData.parentEntity.schema.name,
                            items = listOf(
                                mapOf(FiberyApiConstants.Field.ID.value to childEntity.id)
                            )
                        )
                    )
                )
            )
            .checkResultSuccess()
    }

    private fun FiberyEntityTypeSchema.getUiTitle(): String {
        return requireNotNull(
            this.fields.find { it.meta.isUiTitle }
        ) { "title field name is missing: $this" }.name
    }

    private fun FiberyEntitySortData.toJson(): String {
        if (this.items.isEmpty()) {
            return ""
        }

        val itemsJsons = this.items.map { data -> data.toJson() }

        return "[${itemsJsons.joinToString()}]"
    }

    private fun FiberyEntitySortData.Item.toJson(): String {
        val itemCondition = this.condition.value
        val itemFieldName = this.field.name

        return "[[\"$itemFieldName\"], \"$itemCondition\"]"
    }

    private fun FiberyEntityFilterData.toFilterJson(): String {
        val (mergeType, items) = this
        val itemsJsons = items.mapIndexed { index, data -> data.toFilterJson(index + 1) }

        return "[\"${mergeType.value}\", ${itemsJsons.joinToString()}]"
    }

    private fun FiberyEntityFilterData.Item.toFilterJson(index: Int): String {
        return if (this is FiberyEntityFilterData.Item.SingleSelectItem) {
            val itemCondition = this.condition.value
            val itemFieldName = this.field.name

            val filter =
                "[\"$itemCondition\",[\"$itemFieldName\", \"${FiberyApiConstants.Field.ID.value}\"],\"\$where$index\"]"
            filter
        } else {
            TODO("implement")
        }
    }

    private fun FiberyEntityFilterData.toParamsJson(): String {
        val (_, items) = this
        val itemsJsons = items.mapIndexed { index, data -> data.toParamsJson(index + 1) }

        return "{${itemsJsons.joinToString()}}"
    }

    private fun FiberyEntityFilterData.Item.toParamsJson(index: Int): String {
        return if (this is FiberyEntityFilterData.Item.SingleSelectItem) {
            val itemId = this.param.id

            val params = "\"\$where$index\": \"$itemId\""

            params
        } else {
            TODO("implement")
        }
    }

    companion object {
        private const val PARAM_ID = "\$id"
    }
}
