package by.krossovochkin.fiberyunofficial.entitydetails.data

import android.annotation.SuppressLint
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiConstants
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiRepository
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommand
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyDocumentResponse
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandArgsDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandArgsQueryDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandBody
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyResponseEntityDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyTypeDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyUpdateCommandArgsDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyUpdateCommandBody
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityDetailsData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData
import by.krossovochkin.fiberyunofficial.entitydetails.domain.EntityDetailsRepository
import retrofit2.await
import java.math.BigDecimal
import java.text.SimpleDateFormat

class EntityDetailsRepositoryImpl(
    private val fiberyServiceApi: FiberyServiceApi,
    private val fiberyApiRepository: FiberyApiRepository
) : EntityDetailsRepository {

    override suspend fun getEntityDetails(entityData: FiberyEntityData): FiberyEntityDetailsData {
        val dto = getEntityDetailsDto(entityData)

        val documentSchema = getDocumentFieldSchema(entityData)
        val documentData = FieldData.RichTextFieldData(
            title = documentSchema.name.normalizeTitle(),
            value = getDocumentDto(documentSchema, entityData)?.content ?: "",
            schema = documentSchema
        )

        return mapEntityDetailsData(
            dto = dto,
            entityData = entityData,
            documentData = documentData
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

        return fiberyServiceApi.getEntities(
            listOf(
                FiberyRequestCommandBody(
                    command = FiberyCommand.QUERY_ENTITY.value,
                    args = FiberyRequestCommandArgsDto(
                        FiberyRequestCommandArgsQueryDto(
                            from = entityData.schema.name,
                            select = primitives + enums + relations + collections,
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
                    .fields.find { it.meta.isUiTitle }!!.name
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

    private fun getDocumentFieldSchema(
        entityData: FiberyEntityData
    ): FiberyFieldSchema {
        return entityData.schema.fields.find { it.type == FiberyApiConstants.FieldType.COLLABORATION_DOCUMENT.value }!!
    }

    private suspend fun getDocumentDto(
        documentSchema: FiberyFieldSchema,
        entityData: FiberyEntityData
    ): FiberyDocumentResponse? {
        val documentResponse = fiberyServiceApi.getEntities(
            listOf(
                FiberyRequestCommandBody(
                    command = FiberyCommand.QUERY_ENTITY.value,
                    args = FiberyRequestCommandArgsDto(
                        FiberyRequestCommandArgsQueryDto(
                            from = entityData.schema.name,
                            select = listOf(
                                mapOf(documentSchema.name to listOf(FiberyApiConstants.Field.DOCUMENT_SECRET.value))
                            ),
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
        ).first().result.first()[documentSchema.name]
        val documentSecret =
            (documentResponse as Map<String, Any>)[FiberyApiConstants.Field.DOCUMENT_SECRET.value] as String

        return fiberyServiceApi.getDocument(documentSecret).await()
    }

    private suspend fun mapEntityDetailsData(
        dto: FiberyResponseEntityDto,
        entityData: FiberyEntityData,
        documentData: FieldData.RichTextFieldData
    ): FiberyEntityDetailsData {
        return dto.result.map {
            val titleFieldName = entityData.schema.fields.find { it.meta.isUiTitle }!!.name
            val title = it[titleFieldName] as String
            val id = it[FiberyApiConstants.Field.ID.value] as String
            val publicId = it[FiberyApiConstants.Field.PUBLIC_ID.value] as String

            val fields = mapEntityDetailsFields(
                result = it,
                titleFieldName = titleFieldName,
                entityData = entityData
            )

            FiberyEntityDetailsData(
                id = id,
                publicId = publicId,
                title = title,
                fields = listOf(documentData) +
                        fields
                            .sortedBy { field: FieldData -> field.schema.meta.uiOrder },
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
                val fieldSchema = entityData.schema.fields
                    .find { field: FiberyFieldSchema ->
                        field.name == it.key || field.name == it.key.unwrapCollectionCount()
                    }!!
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
            fiberyApiRepository.getTypeSchema(fieldSchema.type).meta.isEnum-> {
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
            value = data.value as String,
            schema = fieldSchema
        )
    }

    private fun mapNumberFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any?>
    ): FieldData.NumberFieldData {
        return FieldData.NumberFieldData(
            title = fieldSchema.name.normalizeTitle(),
            value = if (data.value == null) {
                BigDecimal.ZERO
            } else {
                data.value.toString().toBigDecimal()
            },
            schema = fieldSchema
        )
    }

    private fun mapCheckboxFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any?>
    ): FieldData.CheckboxFieldData {
        return FieldData.CheckboxFieldData(
            title = fieldSchema.name.normalizeTitle(),
            value = data.value as? Boolean ?: false,
            schema = fieldSchema
        )
    }

    @SuppressLint("SimpleDateFormat")
    private fun mapDateTimeFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any>
    ): FieldData.DateTimeFieldData {
        val value = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(data.value as String)!!
        return FieldData.DateTimeFieldData(
            title = fieldSchema.name.normalizeTitle(),
            value = value,
            schema = fieldSchema
        )
    }

    private suspend fun mapSingleSelectFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any>
    ): FieldData.SingleSelectFieldData {
        val value =
            (data.value as Map<String, Any>)[FiberyApiConstants.Field.ENUM_NAME.value] as String
        val result = getSingleSelectDto(fieldSchema.type).result
        val values = result
            .map { it as Map<String, String> }
            .map {
                FieldData.SingleSelectItemData(
                    id = it[FiberyApiConstants.Field.ID.value] as String,
                    title = it[FiberyApiConstants.Field.ENUM_NAME.value] as String
                )
            }
        return FieldData.SingleSelectFieldData(
            title = fieldSchema.name.normalizeTitle(),
            value = value,
            values = values,
            schema = fieldSchema
        )
    }

    private suspend fun getSingleSelectDto(
        singleSelectType: String
    ): FiberyResponseEntityDto {
        return fiberyServiceApi
            .getEntities(
                listOf(
                    FiberyRequestCommandBody(
                        command = FiberyCommand.QUERY_ENTITY.value,
                        args = FiberyRequestCommandArgsDto(
                            query = FiberyRequestCommandArgsQueryDto(
                                from = singleSelectType,
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
    }

    private suspend fun mapRelationFieldData(
        fieldSchema: FiberyFieldSchema,
        dataEntry: Map.Entry<String, Any>
    ): FieldData.RelationFieldData {
        val data = dataEntry.value as Map<String, Any>
        val typeSchema = fiberyApiRepository.getTypeSchema(fieldSchema.type)
        return FieldData.RelationFieldData(
            title = fieldSchema.name.normalizeTitle(),
            fiberyEntityData = FiberyEntityData(
                id = data[FiberyApiConstants.Field.ID.value] as String,
                publicId = data[FiberyApiConstants.Field.PUBLIC_ID.value] as String,
                title = data[typeSchema.fields.find { it.meta.isUiTitle }!!.name] as String,
                schema = typeSchema
            ),
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
            count = (data.value as Number).toInt(),
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

    companion object {
        private const val PARAM_ID = "\$id"
        private const val PREFIX_COLLECTION_COUNT = "_collectionCount"
    }
}
