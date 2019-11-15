package by.krossovochkin.fiberyunofficial.applist

import by.krossovochkin.fiberyunofficial.applist.data.AppListDataComponent
import by.krossovochkin.fiberyunofficial.applist.data.AppListDataComponentFactory
import by.krossovochkin.fiberyunofficial.applist.domain.AppListDomainComponentFactory
import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import by.krossovochkin.fiberyunofficial.applist.presentation.AppListFragment
import by.krossovochkin.fiberyunofficial.applist.presentation.AppListPresentationComponent
import by.krossovochkin.fiberyunofficial.applist.presentation.AppListPresentationComponentFactory
import by.krossovochkin.fiberyunofficial.applist.presentation.AppListViewModel
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import dagger.Component

interface AppListGlobalDependencies : GlobalDependencies {

    fun appListParentListener(): AppListParentListener
}

interface AppListParentListener {

    fun onAppSelected(fiberyAppData: FiberyAppData)
}

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



