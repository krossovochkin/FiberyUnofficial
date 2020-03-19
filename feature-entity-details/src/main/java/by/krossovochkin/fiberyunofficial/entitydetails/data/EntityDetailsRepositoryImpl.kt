package by.krossovochkin.fiberyunofficial.entitydetails.data

import android.annotation.SuppressLint
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiConstants
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiRepository
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommand
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandArgsDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandArgsQueryDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandBody
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyResponseEntityDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyUpdateCommandArgsDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyUpdateCommandBody
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityDetailsData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData
import by.krossovochkin.fiberyunofficial.entitydetails.domain.EntityDetailsRepository
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.await

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
    ): FiberyResponseEntityDto {
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
                FiberyRequestCommandBody(
                    command = FiberyCommand.QUERY_ENTITY.value,
                    args = FiberyRequestCommandArgsDto(
                        FiberyRequestCommandArgsQueryDto(
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

    private suspend fun getEntityEnumsQuery(
        entityData: FiberyEntityData
    ): List<Any> {
        return entityData.schema.fields
            .filter { fieldSchema ->
                fiberyApiRepository.getTypeSchema(fieldSchema.type).meta.isEnum
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
                fieldSchema.meta.isRelation && !fieldSchema.meta.isCollection
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
            .filter { fieldSchema ->
                fieldSchema.meta.isRelation && fieldSchema.meta.isCollection
            }
            .map { fieldSchema ->
                mapOf(
                    fieldSchema.name.wrapCollectionCount() to listOf(
                        "q/count", listOf(fieldSchema.name, FiberyApiConstants.Field.ID.value)
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
        dto: FiberyResponseEntityDto,
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
                val fieldSchema = requireNotNull(entityData.schema.fields
                    .find { field: FiberyFieldSchema ->
                        field.name == it.key || field.name == it.key.unwrapCollectionCount()
                    }) { "fieldSchema for key ${it.key} is missing" }
                when (fieldSchema.type) {
                    FiberyApiConstants.FieldType.TEXT.value -> {
                        mapTextFieldData(
                            fieldSchema = fieldSchema,
                            data = it
                        )
                    }
                    FiberyApiConstants.FieldType.NUMBER.value -> {
                        mapNumberFieldData(
                            fieldSchema = fieldSchema,
                            data = it
                        )
                    }
                    FiberyApiConstants.FieldType.CHECKBOX.value -> {
                        mapCheckboxFieldData(
                            fieldSchema = fieldSchema,
                            data = it
                        )
                    }
                    FiberyApiConstants.FieldType.DATE_TIME.value -> {
                        mapDateTimeFieldData(
                            fieldSchema = fieldSchema,
                            data = it
                        )
                    }
                    FiberyApiConstants.FieldType.COLLABORATION_DOCUMENT.value -> {
                        mapRichTextFieldData(
                            fieldSchema = fieldSchema,
                            data = it
                        )
                    }
                    else -> {
                        mapEntityDetailsEntityFields(
                            data = it,
                            fieldSchema = fieldSchema,
                            entityData = entityData
                        )
                    }
                }
            }
    }

    private suspend fun mapEntityDetailsEntityFields(
        data: Map.Entry<String, Any>,
        fieldSchema: FiberyFieldSchema,
        entityData: FiberyEntityData
    ): FieldData? {
        return when {
            fiberyApiRepository.getTypeSchema(fieldSchema.type).meta.isEnum -> {
                mapSingleSelectFieldData(
                    fieldSchema = fieldSchema,
                    data = data
                )
            }
            fieldSchema.meta.isRelation && !fieldSchema.meta.isCollection -> {
                mapRelationFieldData(
                    fieldSchema = fieldSchema,
                    dataEntry = data
                )
            }
            fieldSchema.meta.isRelation && fieldSchema.meta.isCollection -> {
                mapCollectionFieldData(
                    fieldSchema = fieldSchema,
                    data = data,
                    entityData = entityData
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
            title = fieldSchema.name.normalizeTitle(),
            value = data.value as? String,
            schema = fieldSchema
        )
    }

    private fun mapNumberFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any?>
    ): FieldData.NumberFieldData {
        return FieldData.NumberFieldData(
            title = fieldSchema.name.normalizeTitle(),
            value = data.value?.toString()?.toBigDecimal(),
            schema = fieldSchema
        )
    }

    private fun mapCheckboxFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any?>
    ): FieldData.CheckboxFieldData {
        return FieldData.CheckboxFieldData(
            title = fieldSchema.name.normalizeTitle(),
            value = data.value as? Boolean,
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
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            )
        }
        return FieldData.DateTimeFieldData(
            title = fieldSchema.name.normalizeTitle(),
            value = value,
            schema = fieldSchema
        )
    }

    private suspend fun mapRichTextFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any>
    ): FieldData.RichTextFieldData {
        val secret = requireNotNull(
            (data.value as? Map<String, Any>)
                ?.get(FiberyApiConstants.Field.DOCUMENT_SECRET.value)
                    as? String
        ) { "rich text secret is missing" }
        val documentDto = fiberyServiceApi.getDocument(secret).await()

        return FieldData.RichTextFieldData(
            title = fieldSchema.name.normalizeTitle(),
            value = documentDto?.content ?: "",
            schema = fieldSchema
        )
    }

    private suspend fun mapSingleSelectFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any>
    ): FieldData.SingleSelectFieldData {
        return FieldData.SingleSelectFieldData(
            title = fieldSchema.name.normalizeTitle(),
            value = (data.value as? Map<String, Any>)
                ?.get(FiberyApiConstants.Field.ENUM_NAME.value)
                    as? String,
            values = fiberyApiRepository.getSingleSelectValues(fieldSchema.type),
            schema = fieldSchema
        )
    }

    private suspend fun mapRelationFieldData(
        fieldSchema: FiberyFieldSchema,
        dataEntry: Map.Entry<String, Any>
    ): FieldData.RelationFieldData {
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
            title = fieldSchema.name.normalizeTitle(),
            fiberyEntityData = entityData,
            schema = fieldSchema
        )
    }

    private suspend fun mapCollectionFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any>,
        entityData: FiberyEntityData
    ): FieldData.CollectionFieldData {
        return FieldData.CollectionFieldData(
            title = fieldSchema.name.normalizeTitle(),
            count = (data.value as? Number)?.toInt() ?: 0,
            entityTypeSchema = fiberyApiRepository.getTypeSchema(fieldSchema.type),
            entityData = entityData,
            schema = fieldSchema
        )
    }

    @SuppressLint("DefaultLocale")
    private fun String.normalizeTitle(): String {
        return this.substringAfter(FiberyApiConstants.DELIMITER_APP_TYPE)
            .split("-")
            .joinToString(separator = " ") { it.capitalize() }
    }

    private fun String.wrapCollectionCount(): String {
        return this + PREFIX_COLLECTION_COUNT
    }

    private fun String.unwrapCollectionCount(): String {
        return this.substringBefore(PREFIX_COLLECTION_COUNT)
    }

    override suspend fun updateSingleSelect(
        entityData: FiberyEntityData,
        fieldSchema: FiberyFieldSchema,
        singleSelectItem: FieldData.SingleSelectItemData
    ) {
        fiberyServiceApi.updateEntity(
            body = listOf(
                FiberyUpdateCommandBody(
                    command = FiberyCommand.QUERY_UPDATE.value,
                    args = FiberyUpdateCommandArgsDto(
                        type = entityData.schema.name,
                        entity = mapOf(
                            FiberyApiConstants.Field.ID.value to entityData.id,
                            fieldSchema.name to mapOf(
                                FiberyApiConstants.Field.ID.value to singleSelectItem.id
                            )
                        )
                    )
                )
            )
        )
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
