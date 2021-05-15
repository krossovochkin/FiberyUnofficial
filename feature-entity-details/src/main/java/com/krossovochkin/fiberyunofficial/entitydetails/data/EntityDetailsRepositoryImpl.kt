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
package com.krossovochkin.fiberyunofficial.entitydetails.data

import com.krossovochkin.fiberyunofficial.core.data.api.FiberyApiConstants
import com.krossovochkin.fiberyunofficial.core.data.api.FiberyApiRepository
import com.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import com.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommand
import com.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommandArgsDto
import com.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommandArgsQueryDto
import com.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommandBody
import com.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyEntityResponseDto
import com.krossovochkin.fiberyunofficial.core.data.api.dto.checkResultSuccess
import com.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.core.domain.FiberyEntityDetailsData
import com.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.core.domain.FieldData
import com.krossovochkin.fiberyunofficial.core.domain.ParentEntityData
import com.krossovochkin.fiberyunofficial.entitydetails.domain.EntityDetailsRepository
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class EntityDetailsRepositoryImpl(
    private val fiberyServiceApi: FiberyServiceApi,
    private val fiberyApiRepository: FiberyApiRepository
) : EntityDetailsRepository {

    override suspend fun getEntityDetails(entityData: FiberyEntityData): FiberyEntityDetailsData {
        val dto = getEntityDetailsDto(entityData)

        return mapEntityDetailsData(
            dto = dto,
            entityData = entityData
        )
    }

    private suspend fun getEntityDetailsDto(
        entityData: FiberyEntityData
    ): FiberyEntityResponseDto {
        val primitives = getEntityPrimitivesQuery(
            entityData = entityData
        )
        val enums = getEntityEnumsQuery(
            entityData = entityData
        )
        val relations = getEntityRelationsQuery(
            entityData = entityData
        )
        val collections = getEntityCollectionsQuery(
            entityData = entityData
        )
        val richTexts = getEntityRichTextsQuery(
            entityData = entityData
        )

        return fiberyServiceApi.getEntities(
            listOf(
                FiberyCommandBody(
                    command = FiberyCommand.QUERY_ENTITY.value,
                    args = FiberyCommandArgsDto(
                        FiberyCommandArgsQueryDto(
                            from = entityData.schema.name,
                            select = primitives + enums + relations + collections + richTexts,
                            where = listOf(
                                FiberyApiConstants.Operator.EQUALS.value,
                                listOf(FiberyApiConstants.Field.ID.value),
                                PARAM_ID
                            ),
                            limit = 1
                        ),
                        params = mapOf(PARAM_ID to entityData.id)
                    )
                )
            )
        ).first()
    }

    private suspend fun getEntityPrimitivesQuery(
        entityData: FiberyEntityData
    ): List<Any> {
        return entityData.schema.fields
            .filter { fieldSchema ->
                fiberyApiRepository.getTypeSchema(fieldSchema.type).meta.isPrimitive
            }
            .map { it.name }
    }

    @Suppress("UnusedPrivateMember")
    private suspend fun getEntityEnumsQuery(
        entityData: FiberyEntityData
    ): List<Any> {
        return entityData.schema.fields
            .filter { fieldSchema ->
                fiberyApiRepository.getTypeSchema(fieldSchema.type).meta.isEnum &&
                    !fieldSchema.meta.isCollection
            }
            .map { fieldSchema ->
                mapOf(
                    fieldSchema.name to listOf(
                        FiberyApiConstants.Field.ID.value,
                        FiberyApiConstants.Field.ENUM_NAME.value
                    )
                )
            }
    }

    private suspend fun getEntityRelationsQuery(
        entityData: FiberyEntityData
    ): List<Any> {
        return entityData.schema.fields
            .filter { fieldSchema ->
                fieldSchema.meta.isRelation &&
                    !fiberyApiRepository.getTypeSchema(fieldSchema.type).meta.isEnum &&
                    !fieldSchema.meta.isCollection
            }
            .map { fieldSchema ->
                val titleFieldName = fiberyApiRepository.getTypeSchema(fieldSchema.type)
                    .getUiTitle()
                mapOf(
                    fieldSchema.name to listOf(
                        FiberyApiConstants.Field.ID.value,
                        FiberyApiConstants.Field.PUBLIC_ID.value,
                        titleFieldName
                    )
                )
            }
    }

    private fun getEntityCollectionsQuery(
        entityData: FiberyEntityData
    ): List<Any> {
        return entityData.schema.fields
            .filter { fieldSchema -> fieldSchema.meta.isCollection }
            .map { fieldSchema ->
                mapOf(
                    fieldSchema.name.wrapCollectionCount() to listOf(
                        "q/count",
                        listOf(fieldSchema.name, FiberyApiConstants.Field.ID.value)
                    )
                )
            }
    }

    private fun getEntityRichTextsQuery(
        entityData: FiberyEntityData
    ): List<Any> {
        return entityData.schema.fields
            .filter { fieldSchema ->
                fieldSchema.type == FiberyApiConstants.FieldType.COLLABORATION_DOCUMENT.value
            }
            .map { fieldSchema ->
                mapOf(
                    fieldSchema.name to listOf(
                        FiberyApiConstants.Field.DOCUMENT_SECRET.value
                    )
                )
            }
    }

    private suspend fun mapEntityDetailsData(
        dto: FiberyEntityResponseDto,
        entityData: FiberyEntityData
    ): FiberyEntityDetailsData {
        return dto.result.map {
            val titleFieldName = entityData.schema.getUiTitle()
            val title = requireNotNull(it[titleFieldName]) {
                "title is missing"
            } as String
            val id = requireNotNull(it[FiberyApiConstants.Field.ID.value]) {
                "id is missing"
            } as String
            val publicId = requireNotNull(it[FiberyApiConstants.Field.PUBLIC_ID.value]) {
                "publicId is missing"
            } as String

            val fields = mapEntityDetailsFields(
                result = it,
                titleFieldName = titleFieldName,
                entityData = entityData
            )

            FiberyEntityDetailsData(
                id = id,
                publicId = publicId,
                title = title,
                fields = fields.sortedBy { field: FieldData -> field.schema.meta.uiOrder },
                schema = entityData.schema
            )
        }.first()
    }

    private suspend fun mapEntityDetailsFields(
        result: Map<String, Any>,
        titleFieldName: String,
        entityData: FiberyEntityData
    ): List<FieldData> {
        val defaultFieldKeys = listOf(
            titleFieldName,
            FiberyApiConstants.Field.ID.value,
            FiberyApiConstants.Field.PUBLIC_ID.value
        )
        return result
            .filter { it.key !in defaultFieldKeys }
            .mapNotNull {
                val fieldSchema = requireNotNull(
                    entityData.schema.fields
                        .find { field: FiberyFieldSchema ->
                            field.name == it.key || field.name == it.key.unwrapCollectionCount()
                        }
                ) { "fieldSchema for key ${it.key} is missing" }
                when (fieldSchema.type) {
                    FiberyApiConstants.FieldType.TEXT.value -> {
                        mapTextFieldData(fieldSchema = fieldSchema, data = it)
                    }
                    FiberyApiConstants.FieldType.URL.value -> {
                        mapUrlFieldData(fieldSchema = fieldSchema, data = it)
                    }
                    FiberyApiConstants.FieldType.EMAIL.value -> {
                        mapEmailFieldData(fieldSchema = fieldSchema, data = it)
                    }
                    FiberyApiConstants.FieldType.NUMBER_INT.value,
                    FiberyApiConstants.FieldType.NUMBER_DECIMAL.value -> {
                        mapNumberFieldData(fieldSchema = fieldSchema, data = it)
                    }
                    FiberyApiConstants.FieldType.CHECKBOX.value -> {
                        mapCheckboxFieldData(fieldSchema = fieldSchema, data = it)
                    }
                    FiberyApiConstants.FieldType.DATE.value -> {
                        mapDateFieldData(fieldSchema = fieldSchema, data = it)
                    }
                    FiberyApiConstants.FieldType.DATE_TIME.value -> {
                        mapDateTimeFieldData(fieldSchema = fieldSchema, data = it)
                    }
                    FiberyApiConstants.FieldType.DATE_RANGE.value -> {
                        mapDateRangeFieldData(fieldSchema = fieldSchema, data = it)
                    }
                    FiberyApiConstants.FieldType.DATE_TIME_RANGE.value -> {
                        mapDateTimeRangeFieldData(fieldSchema = fieldSchema, data = it)
                    }
                    FiberyApiConstants.FieldType.COLLABORATION_DOCUMENT.value -> {
                        mapRichTextFieldData(fieldSchema = fieldSchema, data = it)
                    }
                    else -> {
                        mapEntityDetailsEntityFields(
                            data = it,
                            fieldSchema = fieldSchema
                        )
                    }
                }
            }
    }

    private suspend fun mapEntityDetailsEntityFields(
        data: Map.Entry<String, Any>,
        fieldSchema: FiberyFieldSchema
    ): FieldData? {
        return when {
            fieldSchema.meta.isCollection -> {
                mapCollectionFieldData(
                    fieldSchema = fieldSchema,
                    data = data
                )
            }
            fiberyApiRepository.getTypeSchema(fieldSchema.type).meta.isEnum -> {
                mapSingleSelectFieldData(
                    fieldSchema = fieldSchema,
                    data = data
                )
            }
            fieldSchema.meta.isRelation -> {
                mapRelationFieldData(
                    fieldSchema = fieldSchema,
                    dataEntry = data
                )
            }
            else -> {
                null
            }
        }
    }

    private fun mapTextFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any>
    ): FieldData.TextFieldData {
        return FieldData.TextFieldData(
            title = fieldSchema.displayName,
            value = data.value as? String,
            schema = fieldSchema
        )
    }

    private fun mapUrlFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any>
    ): FieldData.UrlFieldData {
        return FieldData.UrlFieldData(
            title = fieldSchema.displayName,
            value = data.value as? String,
            schema = fieldSchema
        )
    }

    private fun mapEmailFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any>
    ): FieldData.EmailFieldData {
        return FieldData.EmailFieldData(
            title = fieldSchema.displayName,
            value = data.value as? String,
            schema = fieldSchema
        )
    }

    private fun mapNumberFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any?>
    ): FieldData.NumberFieldData {
        return FieldData.NumberFieldData(
            title = fieldSchema.displayName,
            value = data.value?.toString()?.toBigDecimal(),
            unit = fieldSchema.meta.numberUnit,
            precision = if (fieldSchema.type == FiberyApiConstants.FieldType.NUMBER_INT.value) {
                // server responds with precision 1 for unknown reason
                0
            } else {
                fieldSchema.meta.numberPrecision
            },
            schema = fieldSchema
        )
    }

    private fun mapCheckboxFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any?>
    ): FieldData.CheckboxFieldData {
        return FieldData.CheckboxFieldData(
            title = fieldSchema.displayName,
            value = data.value as? Boolean,
            schema = fieldSchema
        )
    }

    private fun mapDateFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any>
    ): FieldData.DateFieldData {
        val value = (data.value as? String)?.let {
            LocalDate.parse(
                it,
                DateTimeFormatter.ofPattern(FiberyApiConstants.Format.DATE.value)
            )
        }
        return FieldData.DateFieldData(
            title = fieldSchema.displayName,
            value = value,
            schema = fieldSchema
        )
    }

    private fun mapDateTimeFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any>
    ): FieldData.DateTimeFieldData {
        val value = (data.value as? String)?.let {
            LocalDateTime.parse(
                it,
                DateTimeFormatter.ofPattern(FiberyApiConstants.Format.DATE_TIME.value)
            )
        }
        return FieldData.DateTimeFieldData(
            title = fieldSchema.displayName,
            value = value,
            schema = fieldSchema
        )
    }

    private fun mapDateRangeFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any>
    ): FieldData.DateRangeFieldData {
        @Suppress("UNCHECKED_CAST")
        val rangeMap = data.value as? Map<String, String>
        val start = rangeMap?.get(FiberyApiConstants.Field.START.value)?.let {
            LocalDate.parse(
                it,
                DateTimeFormatter.ofPattern(FiberyApiConstants.Format.DATE.value)
            )
        }
        val end = rangeMap?.get(FiberyApiConstants.Field.END.value)?.let {
            LocalDate.parse(
                it,
                DateTimeFormatter.ofPattern(FiberyApiConstants.Format.DATE.value)
            )
        }
        return FieldData.DateRangeFieldData(
            title = fieldSchema.displayName,
            start = start,
            end = end,
            schema = fieldSchema
        )
    }

    private fun mapDateTimeRangeFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any>
    ): FieldData.DateTimeRangeFieldData {
        @Suppress("UNCHECKED_CAST")
        val rangeMap = data.value as? Map<String, String>
        val start = rangeMap?.get(FiberyApiConstants.Field.START.value)?.let {
            LocalDateTime.parse(
                it,
                DateTimeFormatter.ofPattern(FiberyApiConstants.Format.DATE_TIME.value)
            )
        }
        val end = rangeMap?.get(FiberyApiConstants.Field.END.value)?.let {
            LocalDateTime.parse(
                it,
                DateTimeFormatter.ofPattern(FiberyApiConstants.Format.DATE_TIME.value)
            )
        }
        return FieldData.DateTimeRangeFieldData(
            title = fieldSchema.displayName,
            start = start,
            end = end,
            schema = fieldSchema
        )
    }

    private suspend fun mapRichTextFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any>
    ): FieldData.RichTextFieldData {
        val secret = requireNotNull(
            @Suppress("UNCHECKED_CAST")
            (data.value as? Map<String, Any>)
                ?.get(FiberyApiConstants.Field.DOCUMENT_SECRET.value)
                as? String
        ) { "rich text secret is missing" }
        val documentDto = runCatching { fiberyServiceApi.getDocument(secret) }.getOrNull()

        return FieldData.RichTextFieldData(
            title = fieldSchema.displayName,
            value = documentDto?.content ?: "",
            schema = fieldSchema
        )
    }

    private suspend fun mapSingleSelectFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any>
    ): FieldData.SingleSelectFieldData {
        val values = fiberyApiRepository.getEnumValues(fieldSchema.type)

        @Suppress("UNCHECKED_CAST")
        val selectedValue = (data.value as? Map<String, String>)
            ?.get(FiberyApiConstants.Field.ID.value)
            ?.let { id -> values.find { value -> value.id == id } }
        return FieldData.SingleSelectFieldData(
            title = fieldSchema.displayName,
            selectedValue = selectedValue,
            values = values,
            schema = fieldSchema
        )
    }

    @Suppress("UnusedPrivateMember")
    private suspend fun mapMultiSelectFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any>
    ): FieldData.MultiSelectFieldData {
        val values = fiberyApiRepository.getEnumValues(fieldSchema.type)

        @Suppress("UNCHECKED_CAST")
        val selectedValues = (
            (data.value as? Map<String, Any>)
                ?.get(FiberyApiConstants.Field.ID.value) as? List<Map<String, String>>
            )
            ?.map { it[FiberyApiConstants.Field.ID.value] }
            ?.map { id -> values.first { value -> value.id == id } }
            ?: emptyList()
        return FieldData.MultiSelectFieldData(
            title = fieldSchema.displayName,
            selectedValues = selectedValues,
            values = values,
            schema = fieldSchema
        )
    }

    private suspend fun mapRelationFieldData(
        fieldSchema: FiberyFieldSchema,
        dataEntry: Map.Entry<String, Any>
    ): FieldData.RelationFieldData {
        @Suppress("UNCHECKED_CAST")
        val data = dataEntry.value as? Map<String, Any>
        val typeSchema = fiberyApiRepository.getTypeSchema(fieldSchema.type)
        val id = data?.get(FiberyApiConstants.Field.ID.value) as? String
        val publicId = data?.get(FiberyApiConstants.Field.PUBLIC_ID.value) as? String
        val title = data?.get(typeSchema.getUiTitle()) as? String

        val entityData = if (id != null && publicId != null && title != null) {
            FiberyEntityData(
                id = id,
                publicId = publicId,
                title = title,
                schema = typeSchema
            )
        } else {
            null
        }

        return FieldData.RelationFieldData(
            title = fieldSchema.displayName,
            fiberyEntityData = entityData,
            schema = fieldSchema
        )
    }

    private suspend fun mapCollectionFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any>
    ): FieldData.CollectionFieldData {
        return FieldData.CollectionFieldData(
            title = fieldSchema.displayName,
            count = (data.value as? Number)?.toInt() ?: 0,
            entityTypeSchema = fiberyApiRepository.getTypeSchema(fieldSchema.type),
            schema = fieldSchema
        )
    }

    private fun String.wrapCollectionCount(): String {
        return this + PREFIX_COLLECTION_COUNT
    }

    private fun String.unwrapCollectionCount(): String {
        return this.substringBefore(PREFIX_COLLECTION_COUNT)
    }

    override suspend fun updateSingleSelectField(
        parentEntityData: ParentEntityData,
        singleSelectItem: FieldData.EnumItemData
    ) {
        fiberyServiceApi
            .sendCommand(
                body = listOf(
                    FiberyCommandBody(
                        command = FiberyCommand.QUERY_UPDATE.value,
                        args = FiberyCommandArgsDto(
                            type = parentEntityData.parentEntity.schema.name,
                            entity = mapOf(
                                FiberyApiConstants.Field.ID.value to parentEntityData.parentEntity.id,
                                parentEntityData.fieldSchema.name to mapOf(
                                    FiberyApiConstants.Field.ID.value to singleSelectItem.id
                                )
                            ),
                            field = parentEntityData.fieldSchema.name
                        )
                    )
                )
            )
            .checkResultSuccess()
    }

    override suspend fun updateEntityField(
        parentEntityData: ParentEntityData,
        selectedEntity: FiberyEntityData?
    ) {
        fiberyServiceApi
            .sendCommand(
                body = listOf(
                    FiberyCommandBody(
                        command = FiberyCommand.QUERY_UPDATE.value,
                        args = FiberyCommandArgsDto(
                            type = parentEntityData.parentEntity.schema.name,
                            entity = mapOf(
                                FiberyApiConstants.Field.ID.value to parentEntityData.parentEntity.id,
                                parentEntityData.fieldSchema.name to mapOf(
                                    FiberyApiConstants.Field.ID.value to selectedEntity?.id
                                )
                            ),
                            field = parentEntityData.fieldSchema.name
                        )
                    )
                )
            )
            .checkResultSuccess()
    }

    override suspend fun updateMultiSelectField(
        parentEntityData: ParentEntityData,
        addedItems: List<FieldData.EnumItemData>,
        removedItems: List<FieldData.EnumItemData>
    ) {
        val commands = mutableListOf<FiberyCommandBody>()
        if (addedItems.isNotEmpty()) {
            commands.add(
                FiberyCommandBody(
                    command = FiberyCommand.QUERY_ADD_COLLECTION_ITEM.value,
                    args = FiberyCommandArgsDto(
                        type = parentEntityData.parentEntity.schema.name,
                        field = parentEntityData.fieldSchema.name,
                        items = addedItems.map { mapOf(FiberyApiConstants.Field.ID.value to it.id) },
                        entity = mapOf(
                            FiberyApiConstants.Field.ID.value to parentEntityData.parentEntity.id
                        )
                    )
                )
            )
        }
        if (removedItems.isNotEmpty()) {
            commands.add(
                FiberyCommandBody(
                    command = FiberyCommand.QUERY_REMOVE_COLLECTION_ITEM.value,
                    args = FiberyCommandArgsDto(
                        type = parentEntityData.parentEntity.schema.name,
                        field = parentEntityData.fieldSchema.name,
                        items = removedItems.map { mapOf(FiberyApiConstants.Field.ID.value to it.id) },
                        entity = mapOf(
                            FiberyApiConstants.Field.ID.value to parentEntityData.parentEntity.id
                        )
                    )
                )
            )
        }

        if (commands.isEmpty()) {
            return
        }

        fiberyServiceApi.sendCommand(body = commands).checkResultSuccess()
    }

    override suspend fun deleteEntity(entity: FiberyEntityData) {
        fiberyServiceApi
            .sendCommand(
                body = listOf(
                    FiberyCommandBody(
                        command = FiberyCommand.QUERY_DELETE.value,
                        args = FiberyCommandArgsDto(
                            type = entity.schema.name,
                            entity = mapOf(
                                FiberyApiConstants.Field.ID.value to entity.id
                            )
                        )
                    )
                )
            )
            .checkResultSuccess()
    }

    private fun FiberyEntityTypeSchema.getUiTitle(): String {
        return requireNotNull(
            this.fields.find { it.meta.isUiTitle }
        ) { "title field name is missing: $this" }.name
    }

    companion object {
        private const val PARAM_ID = "\$id"
        private const val PREFIX_COLLECTION_COUNT = "_collectionCount"
    }
}
