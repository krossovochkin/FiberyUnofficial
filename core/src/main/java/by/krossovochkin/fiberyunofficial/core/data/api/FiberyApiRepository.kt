package by.krossovochkin.fiberyunofficial.core.data.api

import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommand
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandArgsDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandArgsQueryDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandBody
import by.krossovochkin.fiberyunofficial.core.data.api.mapper.FiberyEntityTypeMapper
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData

interface FiberyApiRepository {

    suspend fun getTypeSchemas(): List<FiberyEntityTypeSchema>

    suspend fun getTypeSchema(typeName: String): FiberyEntityTypeSchema

    suspend fun getSingleSelectValues(typeName: String): List<FieldData.SingleSelectItemData>
}

internal class FiberyApiRepositoryImpl(
    private val fiberyServiceApi: FiberyServiceApi,
    private val fiberyEntityTypeMapper: FiberyEntityTypeMapper
) : FiberyApiRepository {

    private var typeSchemas: List<FiberyEntityTypeSchema> = emptyList()
    private val singleSelectValues = mutableMapOf<String, List<FieldData.SingleSelectItemData>>()

    override suspend fun getTypeSchemas(): List<FiberyEntityTypeSchema> {
        if (typeSchemas.isEmpty()) {
            typeSchemas = loadTypeSchemas()
        }
        return typeSchemas
    }

    override suspend fun getTypeSchema(typeName: String): FiberyEntityTypeSchema {
        val type = findTypeSchema(typeName)

        if (type == null) {
            typeSchemas = loadTypeSchemas()
        }

        return findTypeSchema(typeName)!!
    }

    override suspend fun getSingleSelectValues(typeName: String): List<FieldData.SingleSelectItemData> {
        return if (typeName in singleSelectValues) {
            singleSelectValues[typeName]!!
        } else {
            val values = loadSingleSelectValues(typeName)
            singleSelectValues[typeName] = values
            values
        }
    }

    private suspend fun loadSingleSelectValues(typeName: String): List<FieldData.SingleSelectItemData> {
        return fiberyServiceApi
            .getEntities(
                listOf(
                    FiberyRequestCommandBody(
                        command = FiberyCommand.QUERY_ENTITY.value,
                        args = FiberyRequestCommandArgsDto(
                            query = FiberyRequestCommandArgsQueryDto(
                                from = typeName,
                                select = listOf(
                                    FiberyApiConstants.Field.ENUM_NAME.value,
                                    FiberyApiConstants.Field.ID.value
                                ),
                                limit = FiberyApiConstants.Limit.NO_LIMIT.value
                            )
                        )
                    )
                )
            )
            .first()
            .result
            .map { it as Map<String, String> }
            .map {
                FieldData.SingleSelectItemData(
                    id = it[FiberyApiConstants.Field.ID.value] as String,
                    title = it[FiberyApiConstants.Field.ENUM_NAME.value] as String
                )
            }
    }

    private suspend fun loadTypeSchemas(): List<FiberyEntityTypeSchema> {
        return fiberyServiceApi.getSchema().first().result.fiberyTypes
            .map(fiberyEntityTypeMapper::map)
    }

    private fun findTypeSchema(typeName: String): FiberyEntityTypeSchema? {
        return typeSchemas.find { it.name == typeName }
    }
}
