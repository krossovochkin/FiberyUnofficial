package by.krossovochkin.fiberyunofficial.di.filelist

import com.krossovochkin.filelist.domain.DownloadFileInteractor
import com.krossovochkin.filelist.domain.GetFileListInteractor
import com.krossovochkin.filelist.presentation.FileListFragment
import com.krossovochkin.filelist.presentation.FileListViewModelFactory
import dagger.Module
import dagger.Provides

@Module
object FileListPresentationModule {

    @JvmStatic
    @Provides
    fun fileListViewModelFactoryProducer(
        getFileListInteractor: GetFileListInteractor,
        downloadFileInteractor: DownloadFileInteractor,
        argsProvider: FileListFragment.ArgsProvider
    ): () -> FileListViewModelFactory {
        return {
            FileListViewModelFactory(
                getFileListInteractor,
                downloadFileInteractor,
                argsProvider.getFileListArgs()
            )
        }
    }
}
