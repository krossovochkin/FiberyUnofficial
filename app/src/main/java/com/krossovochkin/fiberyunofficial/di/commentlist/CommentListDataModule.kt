package com.krossovochkin.fiberyunofficial.di.commentlist

import com.krossovochkin.commentlist.data.CommentListRepositoryImpl
import com.krossovochkin.commentlist.domain.CommentListRepository
import com.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import dagger.Module
import dagger.Provides

@Module
object CommentListDataModule {

    @JvmStatic
    @Provides
    fun commentListRepository(
        fiberyServiceApi: FiberyServiceApi
    ): CommentListRepository {
        return CommentListRepositoryImpl(
            fiberyServiceApi = fiberyServiceApi
        )
    }
}
