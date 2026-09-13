package com.krossovochkin.fiberyunofficial.api

import com.krossovochkin.fiberyunofficial.api.dto.FiberySchemaResponseDto
import com.krossovochkin.fiberyunofficial.api.mapper.FiberyEntityTypeMapper
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.FieldData
import com.krossovochkin.serialization.FiberyJson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.builtins.ListSerializer

class TestFiberyApiRepository : FiberyApiRepository {

    private val mapper = FiberyEntityTypeMapper()
    private val json = FiberyJson.json
    private var typeSchemas: List<FiberyEntityTypeSchema> = emptyList()

    private val _entityUpdates = MutableSharedFlow<String>(extraBufferCapacity = 64)
    override val entityUpdates: SharedFlow<String> = _entityUpdates.asSharedFlow()

    override suspend fun notifyEntityUpdated(entityId: String) {
        _entityUpdates.emit(entityId)
    }

    override suspend fun getTypeSchemas(): List<FiberyEntityTypeSchema> {
        if (typeSchemas.isNotEmpty()) {
            return typeSchemas
        }

        return this::class.java.classLoader!!.getResource("type_schema.json")
            .readText()
            .let {
                @Suppress("BlockingMethodInNonBlockingContext")
                json.decodeFromString(
                    ListSerializer(FiberySchemaResponseDto.serializer()),
                    it,
                )
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
