package by.krossovochkin.fiberyunofficial.entitydetails.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiConstants
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommand
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandArgsDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandArgsQueryDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandBody
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityDetailsData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData
import by.krossovochkin.fiberyunofficial.entitydetails.domain.EntityDetailsRepository
import java.text.SimpleDateFormat

class EntityDetailsRepositoryImpl(
    private val fiberyServiceApi: FiberyServiceApi
) : EntityDetailsRepository {

    override suspend fun getEntityDetails(entityData: FiberyEntityData): FiberyEntityDetailsData {
        val typesSchema = fiberyServiceApi.getSchema().first().result.fiberyTypes
        val dto = fiberyServiceApi.getEntities(
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
                                .map { it.name },
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
                        else -> null
                    }
                }

            FiberyEntityDetailsData(
                id = id,
                publicId = publicId,
                title = title,
                fields = fields,
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
