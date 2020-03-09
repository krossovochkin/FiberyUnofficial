package by.krossovochkin.fiberyunofficial.applist.data

import by.krossovochkin.fiberyunofficial.applist.domain.AppListRepository
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiRepository
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import dagger.Module
import dagger.Provides

@Module
object AppListDataModule {

    @JvmStatic
    @Provides
    fun appListRepository(
        fiberyApiRepository: FiberyApiRepository
    ): AppListRepository {
        return AppListRepositoryImpl(
            fiberyApiRepository
        )
    }
}
