package by.krossovochkin.fiberyunofficial.entitylist.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiConstants
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommand
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandArgsDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandArgsQueryDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandBody
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.entitylist.domain.EntityListRepository

class EntityListRepositoryImpl(
    private val fiberyServiceApi: FiberyServiceApi,
    private val entityListFiltersSortStorage: EntityListFiltersSortStorage
) : EntityListRepository {

    override suspend fun getEntityList(
        entityType: FiberyEntityTypeSchema,
        offset: Int,
        pageSize: Int,
        entityParams: Pair<FiberyFieldSchema, FiberyEntityData>?
    ): List<FiberyEntityData> {
        val uiTitleType = entityType.fields.find { it.meta.isUiTitle }!!.name
        val idType = FiberyApiConstants.Field.ID.value
        val publicIdType = FiberyApiConstants.Field.PUBLIC_ID.value

        val dto = fiberyServiceApi.getEntities(
            listOf(
                FiberyRequestCommandBody(
                    command = FiberyCommand.QUERY_ENTITY.value,
                    args = FiberyRequestCommandArgsDto(
                        FiberyRequestCommandArgsQueryDto(
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
            val title = it[uiTitleType] as String
            val id = it[idType] as String
            val publicId = it[publicIdType] as String
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
                    fiberyServiceApi.getSchema().first().result.fiberyTypes
                        .find { typeSchema -> typeSchema.name == entityType.name }!!
                        .fields.find { fieldSchema -> fieldSchema.meta.relationId == field.meta.relationId }!!
                        .name

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

    companion object {
        private const val PARAM_ID = "\$id"
    }
}
