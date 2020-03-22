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
package by.krossovochkin.fiberyunofficial.core.data.api

import android.content.Context
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommand
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandArgsDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandArgsQueryDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandBody
import by.krossovochkin.fiberyunofficial.core.data.api.mapper.FiberyEntityTypeMapper
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.File

interface FiberyApiRepository {

    suspend fun getTypeSchemas(): List<FiberyEntityTypeSchema>

    suspend fun getTypeSchema(typeName: String): FiberyEntityTypeSchema

    suspend fun getEnumValues(typeName: String): List<FieldData.EnumItemData>
}

private const val FILE_NAME_TYPE_SCHEMAS = "type_schemas.json"

internal class FiberyApiRepositoryImpl(
    private val context: Context,
    private val moshi: Moshi,
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
            moshi
                .adapter<List<FiberyEntityTypeSchema>>(
                    Types.newParameterizedType(List::class.java, FiberyEntityTypeSchema::class.java)
                )
                .fromJson(json)
                ?: emptyList()
        } else {
            emptyList()
        }
    }

    private fun writeTypeSchemas(typeSchemas: List<FiberyEntityTypeSchema>) {
        val json = moshi
            .adapter<List<FiberyEntityTypeSchema>>(
                Types.newParameterizedType(List::class.java, FiberyEntityTypeSchema::class.java)
            )
            .toJson(typeSchemas)
        if (!json.isNullOrEmpty()) {
            typeSchemasFile.writeText(json)
        }
    }

    private suspend fun loadTypeSchemas(): List<FiberyEntityTypeSchema> {
        return fiberyServiceApi.getSchema().first().result.fiberyTypes
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
