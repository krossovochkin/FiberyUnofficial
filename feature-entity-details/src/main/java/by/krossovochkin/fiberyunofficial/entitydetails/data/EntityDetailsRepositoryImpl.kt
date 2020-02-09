package by.krossovochkin.fiberyunofficial.entitydetails.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiConstants
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommand
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyDocumentResponse
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandArgsDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandArgsQueryDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandBody
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyResponseEntityDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyTypeDto
import by.krossovochkin.fiberyunofficial.core.data.api.mapper.FiberyEntityTypeMapper
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityDetailsData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData
import by.krossovochkin.fiberyunofficial.entitydetails.domain.EntityDetailsRepository
import retrofit2.await
import java.text.SimpleDateFormat

class EntityDetailsRepositoryImpl(
    private val fiberyServiceApi: FiberyServiceApi,
    private val entityTypeMapper: FiberyEntityTypeMapper = FiberyEntityTypeMapper()
) : EntityDetailsRepository {


    override suspend fun getEntityDetails(entityData: FiberyEntityData): FiberyEntityDetailsData {
        val typesSchema = getTypesSchema()
        val dto = getEntityDetailsDto(typesSchema, entityData)

        val documentSchema = getDocumentFieldSchema(entityData)
        val documentData = FieldData.RichTextFieldData(
            title = documentSchema.name.normalizeTitle(),
            value = getDocumentDto(documentSchema, entityData)?.content ?: "",
            schema = documentSchema
        )

        return mapEntityDetailsData(
            dto = dto,
            entityData = entityData,
            typesSchema = typesSchema,
            documentData = documentData
        )
    }

    private suspend fun getTypesSchema(): List<FiberyTypeDto> {
        return fiberyServiceApi.getSchema().first().result.fiberyTypes
    }

    private suspend fun getEntityDetailsDto(
        typesSchema: List<FiberyTypeDto>,
        entityData: FiberyEntityData
    ): FiberyResponseEntityDto {
        val primitives = getEntityPrimitivesQuery(
            typesSchema = typesSchema,
            entityData = entityData
        )
        val enums = getEntityEnumsQuery(
            typesSchema = typesSchema,
            entityData = entityData
        )
        val relations = getEntityRelationsQuery(
            typesSchema = typesSchema,
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


    private fun getEntityPrimitivesQuery(
        typesSchema: List<FiberyTypeDto>,
        entityData: FiberyEntityData
    ): List<Any> {
        return entityData.schema.fields
            .filter { fieldSchema ->
                val isPrimitive = typesSchema
                    .find { typeSchema -> typeSchema.name == fieldSchema.type }
                    ?.meta?.isPrimitive ?: false
                isPrimitive
            }
            .map { it.name }
    }

    private fun getEntityEnumsQuery(
        typesSchema: List<FiberyTypeDto>,
        entityData: FiberyEntityData
    ): List<Any> {
        return entityData.schema.fields
            .filter { fieldSchema ->
                val isEnum = typesSchema
                    .find { typeSchema -> typeSchema.name == fieldSchema.type }
                    ?.meta?.isEnum ?: false
                isEnum
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

    private fun getEntityRelationsQuery(
        typesSchema: List<FiberyTypeDto>,
        entityData: FiberyEntityData
    ): List<Any> {
        return entityData.schema.fields
            .filter { fieldSchema ->
                fieldSchema.meta.isRelation && !fieldSchema.meta.isCollection
            }
            .map { fieldSchema ->
                val titleFieldName = typesSchema
                    .find { typeSchema -> typeSchema.name == fieldSchema.type }
                    ?.fields?.find { it.meta.isUiTitle == true }!!.name
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

    private fun mapEntityDetailsData(
        dto: FiberyResponseEntityDto,
        entityData: FiberyEntityData,
        typesSchema: List<FiberyTypeDto>,
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
                entityData = entityData,
                typesSchema = typesSchema
            )

            FiberyEntityDetailsData(
                id = id,
                publicId = publicId,
                title = title,
                fields = fields + documentData,
                schema = entityData.schema
            )
        }.first()
    }

    private fun mapEntityDetailsFields(
        result: Map<String, Any>,
        titleFieldName: String,
        entityData: FiberyEntityData,
        typesSchema: List<FiberyTypeDto>
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
                            entityData = entityData,
                            typesSchema = typesSchema
                        )
                    }
                }
            }
    }

    private fun mapEntityDetailsEntityFields(
        data: Map.Entry<String, Any>,
        typesSchema: List<FiberyTypeDto>,
        fieldSchema: FiberyFieldSchema,
        entityData: FiberyEntityData
    ): FieldData? {
        val typeSchema = typesSchema
            .find { typeSchema -> typeSchema.name == fieldSchema.type }
        return when {
            typeSchema?.meta?.isEnum == true -> {
                mapSingleSelectFieldData(
                    fieldSchema = fieldSchema,
                    data = data
                )
            }
            fieldSchema.meta.isRelation && !fieldSchema.meta.isCollection -> {
                mapRelationFieldData(
                    fieldSchema = fieldSchema,
                    dataEntry = data,
                    typesSchema = typesSchema
                )
            }
            fieldSchema.meta.isRelation && fieldSchema.meta.isCollection -> {
                mapCollectionFieldData(
                    fieldSchema = fieldSchema,
                    data = data,
                    entityData = entityData,
                    typesSchema = typesSchema
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
        data: Map.Entry<String, Any>
    ): FieldData.NumberFieldData {
        return FieldData.NumberFieldData(
            title = fieldSchema.name.normalizeTitle(),
            value = data.value.toString().toBigDecimal(),
            schema = fieldSchema
        )
    }

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

    private fun mapSingleSelectFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any>
    ): FieldData.SingleSelectFieldData {
        val value =
            (data.value as Map<String, Any>)[FiberyApiConstants.Field.ENUM_NAME.value] as String
        return FieldData.SingleSelectFieldData(
            title = fieldSchema.name.normalizeTitle(),
            value = value,
            schema = fieldSchema
        )
    }

    private fun mapRelationFieldData(
        fieldSchema: FiberyFieldSchema,
        dataEntry: Map.Entry<String, Any>,
        typesSchema: List<FiberyTypeDto>
    ): FieldData.RelationFieldData {
        val data = dataEntry.value as Map<String, Any>
        val typeSchema = typesSchema.find { typeSchema ->
            typeSchema.name == fieldSchema.type
        }
        return FieldData.RelationFieldData(
            title = fieldSchema.name.normalizeTitle(),
            fiberyEntityData = FiberyEntityData(
                id = data[FiberyApiConstants.Field.ID.value] as String,
                publicId = data[FiberyApiConstants.Field.PUBLIC_ID.value] as String,
                title = data[typeSchema?.fields?.find { it.meta.isUiTitle == true }!!.name] as String,
                schema = entityTypeMapper.map(typeSchema)
            ),
            schema = fieldSchema
        )
    }

    private fun mapCollectionFieldData(
        fieldSchema: FiberyFieldSchema,
        data: Map.Entry<String, Any>,
        typesSchema: List<FiberyTypeDto>,
        entityData: FiberyEntityData
    ): FieldData.CollectionFieldData {
        return FieldData.CollectionFieldData(
            title = fieldSchema.name.normalizeTitle(),
            count = (data.value as Number).toInt(),
            entityTypeSchema = entityTypeMapper.map(
                typesSchema.find { it.name == fieldSchema.type }!!
            ),
            entityData = entityData,
            schema = fieldSchema
        )
    }

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

    companion object {
        private const val PARAM_ID = "\$id"
        private const val PREFIX_COLLECTION_COUNT = "_collectionCount"
    }
}
