package com.krossovochkin.fiberyunofficial.di.filelist

import android.content.Context
import com.krossovochkin.auth.AuthStorage
import com.krossovochkin.fiberyunofficial.api.FiberyServiceApi
import com.krossovochkin.filelist.data.FileListRepositoryImpl
import com.krossovochkin.filelist.domain.FileListRepository
import dagger.Module
import dagger.Provides

@Module
object FileListDataModule {

    @JvmStatic
    @Provides
    fun fileListRepository(
        context: Context,
        authStorage: AuthStorage,
        fiberyServiceApi: FiberyServiceApi
    ): FileListRepository {
        return FileListRepositoryImpl(
            context = context,
            authStorage = authStorage,
            fiberyServiceApi = fiberyServiceApi
        )
    }
}
