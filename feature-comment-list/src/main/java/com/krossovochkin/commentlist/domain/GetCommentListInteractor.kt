package com.krossovochkin.commentlist.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyCommentData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.ParentEntityData

interface GetCommentListInteractor {

    suspend fun execute(
        entityType: FiberyEntityTypeSchema,
        parentEntityData: ParentEntityData,
        offset: Int,
        pageSize: Int
    ): List<FiberyCommentData>
}

class GetFileListInteractorImpl(
    private val commentListRepository: CommentListRepository
) : GetCommentListInteractor {

    override suspend fun execute(
        entityType: FiberyEntityTypeSchema,
        parentEntityData: ParentEntityData,
        offset: Int,
        pageSize: Int
    ): List<FiberyCommentData> {
        return commentListRepository.getCommentList(entityType, parentEntityData, offset, pageSize)
    }
}
