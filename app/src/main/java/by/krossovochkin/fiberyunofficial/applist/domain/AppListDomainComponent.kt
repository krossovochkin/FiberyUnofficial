package by.krossovochkin.fiberyunofficial.applist.domain

import dagger.Component
import dagger.Module
import dagger.Provides

interface AppListDomainComponentDependencies {

    fun appListRepository(): AppListRepository
}

@Component(
    modules = [AppListDomainModule::class],
    dependencies = [AppListDomainComponentDependencies::class]
)
interface AppListDomainComponent {

    fun getAppListInteractor(): GetAppListInteractor

    @Component.Builder
    interface Builder {

        fun appListDomainComponentDependencies(dependencies: AppListDomainComponentDependencies): Builder

        fun appListDomainModule(appListDomainModule: AppListDomainModule): Builder

        fun build(): AppListDomainComponent
    }
}

@Module
object AppListDomainModule {

    @JvmStatic
    @Provides
    fun getAppListInteractor(
        appListRepository: AppListRepository
    ): GetAppListInteractor {
        return GetAppListInteractorImpl(appListRepository)
    }
}

object AppListDomainComponentFactory {

    fun create(
        dependencies: AppListDomainComponentDependencies
    ): AppListDomainComponent {
        return DaggerAppListDomainComponent.builder()
            .appListDomainComponentDependencies(dependencies)
            .appListDomainModule(AppListDomainModule)
            .build()
    }
}
