package by.krossovochkin.fiberyunofficial.entitylist.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiConstants
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommand
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandArgsDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandArgsQueryDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandBody
import by.krossovochkin.fiberyunofficial.core.domain.*
import by.krossovochkin.fiberyunofficial.entitylist.domain.EntityListRepository

class EntityListRepositoryImpl(
    private val fiberyServiceApi: FiberyServiceApi
) : EntityListRepository {

    override suspend fun getEntityList(
        entityType: FiberyEntityTypeSchema,
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
                            where = entityParams
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
                                ?: EntityListFilters.filtersMap[entityType.name],
                            orderBy = if (entityParams == null) {
                                EntityListFilters.orderMap[entityType.name]
                            } else {
                                null
                            },
                            limit = 100
                        ),
                        params = entityParams
                            ?.let { (_, entity) ->
                                mapOf(PARAM_ID to entity.id)
                            }
                            ?: EntityListFilters.params[entityType.name]
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

    companion object {
        private const val PARAM_ID = "\$id"
    }
}
