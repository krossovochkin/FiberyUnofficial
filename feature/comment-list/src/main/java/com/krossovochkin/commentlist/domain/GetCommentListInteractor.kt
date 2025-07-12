package com.krossovochkin.commentlist.domain

import com.krossovochkin.fiberyunofficial.api.FiberyApiConstants
import com.krossovochkin.fiberyunofficial.api.FiberyServiceApi
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommand
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommandArgsDto
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommandArgsQueryDto
import com.krossovochkin.fiberyunofficial.api.dto.FiberyCommandBody
import com.krossovochkin.fiberyunofficial.domain.FiberyCommentData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

private const val PARAM_ID = "\$id"

class GetCommentListInteractor @Inject constructor(
    private val fiberyServiceApi: FiberyServiceApi,
) {

    suspend fun execute(
        entityType: FiberyEntityTypeSchema,
        parentEntityData: ParentEntityData,
        offset: Int,
        pageSize: Int
    ): List<FiberyCommentData> {
        val id = FiberyApiConstants.Field.ID.value
        val publicId = FiberyApiConstants.Field.PUBLIC_ID.value
        val creationDate = FiberyApiConstants.Field.CREATION_DATE.value
        val author = FiberyApiConstants.Field.COMMENT_AUTHOR.value
        val secret = FiberyApiConstants.Field.COMMENT_SECRET.value

        val dto = fiberyServiceApi.getEntities(
            listOf(
                FiberyCommandBody(
                    command = FiberyCommand.QUERY_ENTITY.value,
                    args = FiberyCommandArgsDto(
                        query = FiberyCommandArgsQueryDto(
                            from = parentEntityData.parentEntity.schema.name,
                            select = mapOf(
                                parentEntityData.fieldSchema.name to FiberyCommandArgsQueryDto(
                                    from = parentEntityData.fieldSchema.name,
                                    select = mapOf(
                                        id to listOf(id),
                                        publicId to listOf(publicId),
                                        creationDate to listOf(creationDate),
                                        secret to listOf(secret),
                                        author to listOf(
                                            author,
                                            FiberyApiConstants.Field.USER_NAME.value
                                        )
                                    ),
                                    offset = offset,
                                    limit = pageSize
                                )
                            ),
                            where = listOf(
                                FiberyApiConstants.Operator.EQUALS.value,
                                listOf(FiberyApiConstants.Field.ID.value),
                                PARAM_ID
                            ),
                            limit = 1
                        ),
                        params = mapOf(PARAM_ID to parentEntityData.parentEntity.id)
                    )
                )
            )
        ).first()

        @Suppress("UNCHECKED_CAST")
        val result = dto.result as List<Map<String, List<Map<String, Any>>>>
        return result.first()
            .flatMap { it.value }
            .map {
                FiberyCommentData(
                    id = id,
                    authorName = it[author] as String,
                    text = fiberyServiceApi.getDocument(secret = it[secret] as String).content,
                    createDate = LocalDateTime.parse(
                        it[creationDate] as String,
                        DateTimeFormatter.ofPattern(FiberyApiConstants.Format.DATE_TIME.value)
                    ),
                    schema = entityType
                )
            }
    }
}
