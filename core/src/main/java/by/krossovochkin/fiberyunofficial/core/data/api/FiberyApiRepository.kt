package by.krossovochkin.fiberyunofficial.core.data.api

import by.krossovochkin.fiberyunofficial.core.data.api.mapper.FiberyEntityTypeMapper
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema

interface FiberyApiRepository {

    suspend fun getTypeSchemas(): List<FiberyEntityTypeSchema>

    suspend fun getTypeSchema(typeName: String): FiberyEntityTypeSchema
}

internal class FiberyApiRepositoryImpl(
    private val fiberyServiceApi: FiberyServiceApi,
    private val fiberyEntityTypeMapper: FiberyEntityTypeMapper
) : FiberyApiRepository {

    private var typeSchemas: List<FiberyEntityTypeSchema> = emptyList()

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

    private suspend fun loadTypeSchemas(): List<FiberyEntityTypeSchema> {
        return fiberyServiceApi.getSchema().first().result.fiberyTypes
            .map(fiberyEntityTypeMapper::map)
    }

    private fun findTypeSchema(typeName: String): FiberyEntityTypeSchema? {
        return typeSchemas.find { it.name == typeName }
    }
}
