package com.krossovochkin.commentlist.domain

import com.krossovochkin.fiberyunofficial.core.domain.FiberyCommentData
import com.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.core.domain.ParentEntityData

interface CommentListRepository {

    suspend fun getCommentList(
        entityType: FiberyEntityTypeSchema,
        parentEntityData: ParentEntityData,
        offset: Int,
        pageSize: Int
    ): List<FiberyCommentData>
}
