package by.krossovochkin.fiberyunofficial.entitycreate.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommand
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCreateCommandArgsDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCreateCommandBody
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.entitycreate.domain.EntityCreateRepository

class EntityCreateRepositoryImpl(
    private val fiberyServiceApi: FiberyServiceApi
) : EntityCreateRepository {

    override suspend fun createEntity(entityTypeSchema: FiberyEntityTypeSchema, name: String) {
        if (name.isEmpty()) {
            throw IllegalArgumentException("name should not be null")
        }
        val titleFieldName = entityTypeSchema.fields
            .find { it.meta.isUiTitle }!!.name
        fiberyServiceApi.createEntity(
            listOf(
                FiberyCreateCommandBody(
                    command = FiberyCommand.QUERY_CREATE.value,
                    args = FiberyCreateCommandArgsDto(
                        type = entityTypeSchema.name,
                        entity = mapOf(
                            titleFieldName to name
                        )
                    )
                )
            )
        )
    }
}
