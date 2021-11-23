package com.krossovochkin.fiberyunofficial.api

import com.krossovochkin.fiberyunofficial.api.dto.FiberySchemaResponseDto
import com.krossovochkin.fiberyunofficial.api.mapper.FiberyEntityTypeMapper
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.FieldData
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class TestFiberyApiRepository : FiberyApiRepository {

    private val mapper = FiberyEntityTypeMapper()
    private val serializer = Moshi.Builder().build()
    private var typeSchemas: List<FiberyEntityTypeSchema> = emptyList()

    override suspend fun getTypeSchemas(): List<FiberyEntityTypeSchema> {
        if (typeSchemas.isNotEmpty()) {
            return typeSchemas
        }

        return this::class.java.classLoader!!.getResource("type_schema.json")
            .readText()
            .let {
                @Suppress("BlockingMethodInNonBlockingContext")
                serializer
                    .adapter<List<FiberySchemaResponseDto>>(
                        Types.newParameterizedType(
                            List::class.java,
                            FiberySchemaResponseDto::class.java
                        )
                    )
                    .fromJson(it)!!
                    .first().result.fiberyTypes
                    .map(mapper::map)
            }
            .also { typeSchemas = it }
    }

    override suspend fun getTypeSchema(typeName: String): FiberyEntityTypeSchema {
        if (typeSchemas.isEmpty()) {
            getTypeSchemas()
        }

        return typeSchemas.find { it.name == typeName }!!
    }

    override suspend fun getEnumValues(typeName: String): List<FieldData.EnumItemData> {
        throw UnsupportedOperationException()
    }
}
