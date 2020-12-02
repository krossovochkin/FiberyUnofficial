package by.krossovochkin.fiberyunofficial.di.commentlist

import com.krossovochkin.commentlist.domain.GetCommentListInteractor
import com.krossovochkin.commentlist.presentation.CommentListFragment
import com.krossovochkin.commentlist.presentation.CommentListViewModelFactory
import dagger.Module
import dagger.Provides

@Module
object CommentListPresentationModule {

    @JvmStatic
    @Provides
    fun commentListViewModelFactoryProducer(
        getCommentListInteractor: GetCommentListInteractor,
        argsProvider: CommentListFragment.ArgsProvider
    ): () -> CommentListViewModelFactory {
        return {
            CommentListViewModelFactory(
                getCommentListInteractor,
                argsProvider.getCommentListArgs()
            )
        }
    }
}
