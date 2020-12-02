package com.krossovochkin.commentlist.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyCommentData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.ParentEntityData

interface CommentListRepository {

    suspend fun getCommentList(
        entityType: FiberyEntityTypeSchema,
        parentEntityData: ParentEntityData,
        offset: Int,
        pageSize: Int
    ): List<FiberyCommentData>
}
