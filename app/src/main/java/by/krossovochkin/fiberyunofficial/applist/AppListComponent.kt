package by.krossovochkin.fiberyunofficial.applist

import by.krossovochkin.fiberyunofficial.app.GlobalDependencies
import by.krossovochkin.fiberyunofficial.applist.data.AppListDataComponent
import by.krossovochkin.fiberyunofficial.applist.data.AppListDataComponentFactory
import by.krossovochkin.fiberyunofficial.applist.domain.AppListDomainComponentFactory
import by.krossovochkin.fiberyunofficial.applist.presentation.*
import dagger.Component

interface AppListGlobalDependencies : GlobalDependencies

@Component(
    dependencies = [
        AppListDataComponent::class,
        AppListPresentationComponent::class
    ]
)
interface AppListComponent {

    fun appListViewModel(): AppListViewModel

    fun inject(fragment: AppListFragment)
}

object AppListComponentFactory {

    fun create(
        fragment: AppListFragment,
        appListGlobalDependencies: AppListGlobalDependencies
    ): AppListComponent {
        val appListDataComponent = AppListDataComponentFactory
            .create(
                appListGlobalDependencies = appListGlobalDependencies
            )
        val appListDomainComponent = AppListDomainComponentFactory
            .create(
                dependencies = appListDataComponent
            )
        val appListPresentationComponent = AppListPresentationComponentFactory
            .create(
                fragment = fragment,
                appListDomainComponent = appListDomainComponent,
                appListGlobalDependencies = appListGlobalDependencies
            )
        return DaggerAppListComponent.builder()
            .appListDataComponent(appListDataComponent)
            .appListPresentationComponent(appListPresentationComponent)
            .build()
    }
}



