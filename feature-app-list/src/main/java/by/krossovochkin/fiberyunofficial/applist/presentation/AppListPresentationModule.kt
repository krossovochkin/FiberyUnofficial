package by.krossovochkin.fiberyunofficial.applist.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import by.krossovochkin.fiberyunofficial.applist.AppListParentListener
import by.krossovochkin.fiberyunofficial.applist.domain.GetAppListInteractor
import dagger.Module
import dagger.Provides


@Module
object AppListPresentationModule {

    @JvmStatic
    @Provides
    fun appListViewModel(
        fragment: Fragment,
        appListViewModelFactory: AppListViewModelFactory
    ): AppListViewModel {
        return ViewModelProviders
            .of(fragment, appListViewModelFactory)
            .get()
    }

    @JvmStatic
    @Provides
    fun appListViewModelFactory(
        getAppListInteractor: GetAppListInteractor,
        appListParentListener: AppListParentListener
    ): AppListViewModelFactory {
        return AppListViewModelFactory(
            getAppListInteractor,
            appListParentListener
        )
    }
}
