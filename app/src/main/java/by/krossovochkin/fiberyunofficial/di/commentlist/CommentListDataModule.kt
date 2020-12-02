package by.krossovochkin.fiberyunofficial.di.commentlist

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import com.krossovochkin.commentlist.data.CommentListRepositoryImpl
import com.krossovochkin.commentlist.domain.CommentListRepository
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
