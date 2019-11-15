package by.krossovochkin.fiberyunofficial.applist

import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.applist.data.AppListDataModule
import by.krossovochkin.fiberyunofficial.applist.domain.AppListDomainModule
import by.krossovochkin.fiberyunofficial.applist.presentation.AppListFragment
import by.krossovochkin.fiberyunofficial.applist.presentation.AppListPresentationModule
import by.krossovochkin.fiberyunofficial.applist.presentation.AppListViewModel
import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

interface AppListGlobalDependencies : GlobalDependencies {

    fun appListParentListener(): AppListViewModel.ParentListener
}

@AppList
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

@Scope
@Retention
annotation class AppList

