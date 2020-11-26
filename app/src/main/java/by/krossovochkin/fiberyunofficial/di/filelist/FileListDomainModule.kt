package by.krossovochkin.fiberyunofficial.di.filelist

import com.krossovochkin.filelist.domain.DownloadFileInteractor
import com.krossovochkin.filelist.domain.DownloadFileInteractorImpl
import com.krossovochkin.filelist.domain.FileListRepository
import com.krossovochkin.filelist.domain.GetFileListInteractor
import com.krossovochkin.filelist.domain.GetFileListInteractorImpl
import dagger.Module
import dagger.Provides

@Module
object FileListDomainModule {

    @JvmStatic
    @Provides
    fun getFilterListInteractor(
        fileListRepository: FileListRepository
    ): GetFileListInteractor {
        return GetFileListInteractorImpl(fileListRepository)
    }

    @JvmStatic
    @Provides
    fun downloadFileInteractor(
        fileListRepository: FileListRepository
    ): DownloadFileInteractor {
        return DownloadFileInteractorImpl(fileListRepository)
    }
}
