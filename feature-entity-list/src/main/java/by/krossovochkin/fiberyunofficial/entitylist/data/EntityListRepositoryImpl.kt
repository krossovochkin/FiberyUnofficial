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
        entityType: FiberyEntityTypeSchema
    ): List<FiberyEntityData> {
        val dto = fiberyServiceApi.getEntities(
            listOf(
                FiberyRequestCommandBody(
                    command = FiberyCommand.QUERY_ENTITY.value,
                    args = FiberyRequestCommandArgsDto(
                        FiberyRequestCommandArgsQueryDto(
                            from = entityType.name,
                            select = entityType.fields
                                .filter { !it.meta.isCollection && !it.meta.isRelation && it.type != FiberyApiConstants.FieldType.COLLABORATION_DOCUMENT.value }
                                .map { it.name },
                            limit = 100
                        )
                    )
                )
            )
        ).first()

        return dto.result.map {
            val title = it[entityType.fields.find { it.meta.isUiTitle }!!.name] as String
            val id = it[FiberyApiConstants.Field.ID.value] as String
            val publicId = it[FiberyApiConstants.Field.PUBLIC_IC.value] as String
            FiberyEntityData(
                id = id,
                publicId = publicId,
                title = title,
                schema = entityType
            )
        }
    }
}
