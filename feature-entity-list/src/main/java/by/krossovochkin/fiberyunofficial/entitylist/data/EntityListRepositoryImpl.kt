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
package by.krossovochkin.fiberyunofficial.entitylist.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiConstants
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiRepository
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommand
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommandArgsDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommandArgsQueryDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommandBody
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.entitylist.domain.EntityListRepository

class EntityListRepositoryImpl(
    private val fiberyServiceApi: FiberyServiceApi,
    private val fiberyApiRepository: FiberyApiRepository,
    private val entityListFiltersSortStorage: EntityListFiltersSortStorage
) : EntityListRepository {

    override suspend fun getEntityList(
        entityType: FiberyEntityTypeSchema,
        offset: Int,
        pageSize: Int,
        entityParams: Pair<FiberyFieldSchema, FiberyEntityData>?
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
                            where = getQueryWhere(
                                entityType = entityType,
                                entityParams = entityParams
                            ),
                            orderBy = getQueryOrderBy(
                                entityType = entityType,
                                entityParams = entityParams
                            ),
                            offset = offset,
                            limit = pageSize
                        ),
                        params = getQueryParams(
                            entityType = entityType,
                            entityParams = entityParams
                        )
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

    private suspend fun getQueryWhere(
        entityType: FiberyEntityTypeSchema,
        entityParams: Pair<FiberyFieldSchema, FiberyEntityData>?
    ): List<Any>? {
        return entityParams
            ?.let { (field, _) ->
                val fieldName =
                    requireNotNull(fiberyApiRepository.getTypeSchema(entityType.name)
                        .fields.find { fieldSchema -> fieldSchema.meta.relationId == field.meta.relationId }
                    ) { "relation wasn't found" }.name

                listOf(
                    FiberyApiConstants.Operator.EQUALS.value,
                    listOf(
                        fieldName,
                        FiberyApiConstants.Field.ID.value
                    ),
                    PARAM_ID
                )
            }
            ?: entityListFiltersSortStorage.getFilter(entityType.name)
            ?: EntityListFilters.filtersMap[entityType.name]
    }

    private fun getQueryOrderBy(
        entityType: FiberyEntityTypeSchema,
        entityParams: Pair<FiberyFieldSchema, FiberyEntityData>?
    ): List<Any>? {
        return if (entityParams == null) {
            entityListFiltersSortStorage.getSort(entityType.name)
                ?: EntityListFilters.orderMap[entityType.name]
        } else {
            null
        }
    }

    private fun getQueryParams(
        entityType: FiberyEntityTypeSchema,
        entityParams: Pair<FiberyFieldSchema, FiberyEntityData>?
    ): Map<String, Any>? {
        return entityParams
            ?.let { (_, entity) ->
                mapOf(PARAM_ID to entity.id)
            }
            ?: entityListFiltersSortStorage.getParams(entityType.name)
            ?: EntityListFilters.params[entityType.name]
    }

    override fun setEntityListFilter(
        entityType: FiberyEntityTypeSchema,
        filter: String,
        params: String
    ) {
        entityListFiltersSortStorage.setFilter(entityType.name, filter, params)
    }

    override fun setEntityListSort(entityType: FiberyEntityTypeSchema, sort: String) {
        entityListFiltersSortStorage.setSort(entityType.name, sort)
    }

    override fun getEntityListFilter(entityType: FiberyEntityTypeSchema): Pair<String, String> {
        return entityListFiltersSortStorage.getRawFilter(entityType.name) to
                entityListFiltersSortStorage.getRawParams(entityType.name)
    }

    override fun getEntityListSort(entityType: FiberyEntityTypeSchema): String {
        return entityListFiltersSortStorage.getRawSort(entityType.name)
    }

    private fun FiberyEntityTypeSchema.getUiTitle(): String {
        return requireNotNull(
            this.fields.find { it.meta.isUiTitle }
        ) { "title field name is missing: $this" }.name
    }

    companion object {
        private const val PARAM_ID = "\$id"
    }
}
