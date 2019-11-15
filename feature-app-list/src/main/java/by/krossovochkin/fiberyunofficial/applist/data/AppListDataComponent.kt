package by.krossovochkin.fiberyunofficial.applist.data

import by.krossovochkin.fiberyunofficial.applist.AppListGlobalDependencies
import by.krossovochkin.fiberyunofficial.applist.domain.AppListDomainComponentDependencies
import by.krossovochkin.fiberyunofficial.applist.domain.AppListRepository
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import dagger.Component
import dagger.Module
import dagger.Provides

@Component(
    modules = [AppListDataModule::class],
    dependencies = [AppListGlobalDependencies::class]
)
interface AppListDataComponent :
    AppListDomainComponentDependencies

@Module
object AppListDataModule {

    @JvmStatic
    @Provides
    fun appListRepository(
        fiberyServiceApi: FiberyServiceApi
    ): AppListRepository {
        return AppListRepositoryImpl(
            fiberyServiceApi
        )
    }
}

object AppListDataComponentFactory {

    fun create(
        appListGlobalDependencies: AppListGlobalDependencies
    ): AppListDataComponent {
        return DaggerAppListDataComponent.builder()
            .appListGlobalDependencies(appListGlobalDependencies)
            .build()
    }
}