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
package com.krossovochkin.fiberyunofficial.api

import android.content.Context
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommand
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommandArgsDto
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommandArgsQueryDto
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommandBody
import com.krossovochkin.fiberyunofficial.api.mapper.FiberyEntityTypeMapper
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.FieldData
import java.io.File

interface FiberyApiRepository {

    suspend fun getTypeSchemas(): List<FiberyEntityTypeSchema>

    suspend fun getTypeSchema(typeName: String): FiberyEntityTypeSchema

    suspend fun getEnumValues(typeName: String): List<FieldData.EnumItemData>
}

private const val FILE_NAME_TYPE_SCHEMAS = "type_schemas.json"

class FiberyApiRepositoryImpl(
    private val context: Context,
    private val serializer: com.krossovochkin.serialization.Serializer,
    private val fiberyServiceApi: FiberyServiceApi,
    private val fiberyEntityTypeMapper: FiberyEntityTypeMapper
) : FiberyApiRepository {

    private var typeSchemas: List<FiberyEntityTypeSchema> = emptyList()
    private val typeSchemasFile: File
        get() = File(context.cacheDir, FILE_NAME_TYPE_SCHEMAS)

    private val singleSelectValues = mutableMapOf<String, List<FieldData.EnumItemData>>()

    override suspend fun getTypeSchemas(): List<FiberyEntityTypeSchema> {
        if (typeSchemas.isNotEmpty()) {
            return typeSchemas
        }

        val persistedTypeSchemas = readTypeSchemas()
        if (persistedTypeSchemas.isNotEmpty()) {
            typeSchemas = persistedTypeSchemas
            return typeSchemas
        }

        val loadedTypeSchemas = loadTypeSchemas()
        writeTypeSchemas(loadedTypeSchemas)
        typeSchemas = loadedTypeSchemas

        return typeSchemas
    }

    override suspend fun getTypeSchema(typeName: String): FiberyEntityTypeSchema {
        val type = findTypeSchema(typeName)

        if (type == null) {
            invalidate()
            typeSchemas = getTypeSchemas()
        }

        return findTypeSchema(typeName)!!
    }

    override suspend fun getEnumValues(typeName: String): List<FieldData.EnumItemData> {
        return if (typeName in singleSelectValues) {
            singleSelectValues[typeName]!!
        } else {
            val values = loadSingleSelectValues(typeName)
            singleSelectValues[typeName] = values
            values
        }
    }

    private suspend fun loadSingleSelectValues(typeName: String): List<FieldData.EnumItemData> {
        return fiberyServiceApi
            .getEntities(
                listOf(
                    FiberyCommandBody(
                        command = FiberyCommand.QUERY_ENTITY.value,
                        args = FiberyCommandArgsDto(
                            query = FiberyCommandArgsQueryDto(
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
            .map {
                @Suppress("UNCHECKED_CAST")
                it as Map<String, String>
            }
            .map {
                FieldData.EnumItemData(
                    id = it[FiberyApiConstants.Field.ID.value] as String,
                    title = it[FiberyApiConstants.Field.ENUM_NAME.value] as String
                )
            }
    }

    private fun readTypeSchemas(): List<FiberyEntityTypeSchema> {
        val json = if (typeSchemasFile.exists()) typeSchemasFile.readText() else null
        return if (!json.isNullOrEmpty()) {
            return serializer.jsonToList(json, FiberyEntityTypeSchema::class.java)
        } else {
            emptyList()
        }
    }

    private fun writeTypeSchemas(typeSchemas: List<FiberyEntityTypeSchema>) {
        val json = serializer.listToJson(typeSchemas, FiberyEntityTypeSchema::class.java)
        if (json.isNotEmpty()) {
            typeSchemasFile.writeText(json)
        }
    }

    private suspend fun loadTypeSchemas(): List<FiberyEntityTypeSchema> {
        return fiberyServiceApi
            .getSchema(listOf(FiberyCommandBody(command = FiberyCommand.QUERY_SCHEMA.value)))
            .first().result.fiberyTypes
            .map(fiberyEntityTypeMapper::map)
    }

    private fun invalidate() {
        typeSchemas = emptyList()
        typeSchemasFile.delete()
    }

    private fun findTypeSchema(typeName: String): FiberyEntityTypeSchema? {
        return typeSchemas.find { it.name == typeName }
    }
}
