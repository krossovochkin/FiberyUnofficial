package com.krossovochkin.commentlist.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiConstants
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommand
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommandArgsDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommandArgsQueryDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommandBody
import by.krossovochkin.fiberyunofficial.core.domain.FiberyCommentData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.ParentEntityData
import com.krossovochkin.commentlist.domain.CommentListRepository
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

private const val PARAM_ID = "\$id"

class CommentListRepositoryImpl(
    private val fiberyServiceApi: FiberyServiceApi
) : CommentListRepository {

    override suspend fun getCommentList(
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
