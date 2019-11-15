package by.krossovochkin.fiberyunofficial.applist

import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.applist.data.AppListDataModule
import by.krossovochkin.fiberyunofficial.applist.domain.AppListDomainModule
import by.krossovochkin.fiberyunofficial.applist.presentation.*
import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import dagger.BindsInstance
import dagger.Component

interface AppListGlobalDependencies : GlobalDependencies {

    fun appListParentListener(): AppListParentListener
}

interface AppListParentListener {

    fun onAppSelected(fiberyAppData: FiberyAppData)
}

@Component(
    modules = [
        AppListDataModule::class,
        AppListDomainModule::class,
        AppListPresentationModule::class
    ],
    dependencies = [AppListGlobalDependencies::class]
)
interface AppListComponent {

    fun appListViewModel(): AppListViewModel

    fun inject(fragment: AppListFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun fragment(fragment: Fragment): Builder

        fun appListGlobalDependencies(appListGlobalDependencies: AppListGlobalDependencies): Builder

        fun build(): AppListComponent
    }
}

object AppListComponentFactory {

    fun create(
        fragment: AppListFragment,
        appListGlobalDependencies: AppListGlobalDependencies
    ): AppListComponent {
        return DaggerAppListComponent.builder()
            .fragment(fragment)
            .appListGlobalDependencies(appListGlobalDependencies)
            .build()
    }
}



