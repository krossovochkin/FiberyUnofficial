package by.krossovochkin.fiberyunofficial.applist.domain

import dagger.Module
import dagger.Provides

@Module
object AppListDomainModule {

    @JvmStatic
    @Provides
    fun getAppListInteractor(
        appListRepository: AppListRepository
    ): GetAppListInteractor {
        return GetAppListInteractorImpl(
            appListRepository
        )
    }
}
