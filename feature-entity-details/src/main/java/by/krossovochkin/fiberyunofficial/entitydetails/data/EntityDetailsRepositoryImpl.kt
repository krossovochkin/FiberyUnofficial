package by.krossovochkin.fiberyunofficial.entitydetails.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiConstants
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommand
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandArgsDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandArgsQueryDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandBody
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityDetailsData
import by.krossovochkin.fiberyunofficial.entitydetails.domain.EntityDetailsRepository

class EntityDetailsRepositoryImpl(
    private val fiberyServiceApi: FiberyServiceApi
) : EntityDetailsRepository {

    override suspend fun getEntityDetails(entityData: FiberyEntityData): FiberyEntityDetailsData {
        val dto = fiberyServiceApi.getEntities(
            listOf(
                FiberyRequestCommandBody(
                    command = FiberyCommand.QUERY_ENTITY.value,
                    args = FiberyRequestCommandArgsDto(
                        FiberyRequestCommandArgsQueryDto(
                            from = entityData.schema.name,
                            select = entityData.schema.fields
                                .filter { !it.meta.isCollection && !it.meta.isRelation && it.type != FiberyApiConstants.FieldType.COLLABORATION_DOCUMENT.value }
                                .map { it.name },
                            where = listOf(
                                FiberyApiConstants.Operator.EQUALS.value,
                                listOf(FiberyApiConstants.Field.ID.value),
                                PARAM_ID
                            ),
                            limit = 1
                        ),
                        params = mapOf(PARAM_ID to entityData.data.getValue(FiberyApiConstants.Field.ID.value))
                    )
                )
            )
        ).first()

        return dto.result.map { FiberyEntityDetailsData(it, entityData.schema) }.first()
    }

    companion object {
        private const val PARAM_ID = "\$id"
    }
}
