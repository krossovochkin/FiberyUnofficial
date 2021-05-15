package com.krossovochkin.fiberyunofficial.di.commentlist

import com.krossovochkin.commentlist.domain.CommentListRepository
import com.krossovochkin.commentlist.domain.GetCommentListInteractor
import com.krossovochkin.commentlist.domain.GetFileListInteractorImpl
import dagger.Module
import dagger.Provides

@Module
object CommentListDomainModule {

    @JvmStatic
    @Provides
    fun getCommentListInteractor(
        commentListRepository: CommentListRepository
    ): GetCommentListInteractor {
        return GetFileListInteractorImpl(commentListRepository)
    }
}
