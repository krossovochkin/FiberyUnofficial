package by.krossovochkin.fiberyunofficial.entitydetails.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiConstants
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.core.data.api.dto.*
import by.krossovochkin.fiberyunofficial.core.domain.*
import by.krossovochkin.fiberyunofficial.entitydetails.domain.EntityDetailsRepository
import retrofit2.await
import java.text.SimpleDateFormat
import java.util.*

class EntityDetailsRepositoryImpl(
    private val fiberyServiceApi: FiberyServiceApi
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
        return fiberyServiceApi.getEntities(
            listOf(
                FiberyRequestCommandBody(
                    command = FiberyCommand.QUERY_ENTITY.value,
                    args = FiberyRequestCommandArgsDto(
                        FiberyRequestCommandArgsQueryDto(
                            from = entityData.schema.name,
                            select = entityData.schema.fields
                                .filter { fieldSchema ->
                                    val isPrimitive = typesSchema
                                        .find { typeSchema -> typeSchema.name == fieldSchema.type }
                                        ?.meta?.isPrimitive ?: false
                                    isPrimitive
                                }
                                .map { it.name } +
                                    entityData.schema.fields
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
                                        },
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

            val defaultFieldKeys = listOf(
                titleFieldName,
                FiberyApiConstants.Field.ID.value,
                FiberyApiConstants.Field.PUBLIC_ID.value
            )

            val fields = it
                .filter { it.key !in defaultFieldKeys }
                .mapNotNull {
                    val fieldSchema = entityData.schema.fields
                        .find { field: FiberyFieldSchema -> field.name == it.key }!!
                    when (fieldSchema.type) {
                        FiberyApiConstants.FieldType.TEXT.value -> {
                            FieldData.TextFieldData(
                                title = fieldSchema.name.normalizeTitle(),
                                value = it.value as String,
                                schema = fieldSchema
                            )
                        }
                        FiberyApiConstants.FieldType.NUMBER.value -> {
                            FieldData.NumberFieldData(
                                title = fieldSchema.name.normalizeTitle(),
                                value = it.value.toString().toBigDecimal(),
                                schema = fieldSchema
                            )
                        }
                        FiberyApiConstants.FieldType.DATE_TIME.value -> {
                            FieldData.DateTimeFieldData(
                                title = fieldSchema.name.normalizeTitle(),
                                value = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(it.value as String)!!,
                                schema = fieldSchema
                            )
                        }
                        else -> {
                            val typeSchema = typesSchema
                                .find { typeSchema -> typeSchema.name == fieldSchema.type }
                            if (typeSchema?.meta?.isEnum == true) {
                                FieldData.SingleSelectFieldData(
                                    title = fieldSchema.name.normalizeTitle(),
                                    value = (it.value as Map<String, Any>)[FiberyApiConstants.Field.ENUM_NAME.value] as String,
                                    schema = fieldSchema
                                )
                            } else {
                                null
                            }
                        }
                    }
                }

            FiberyEntityDetailsData(
                id = id,
                publicId = publicId,
                title = title,
                fields = fields + documentData,
                schema = entityData.schema
            )
        }.first()
    }

    private fun String.normalizeTitle(): String {
        return this.substringAfter(FiberyApiConstants.DELIMITER_APP_TYPE)
            .split("-")
            .joinToString(separator = " ") { it.capitalize() }
    }

    companion object {
        private const val PARAM_ID = "\$id"
    }
}
