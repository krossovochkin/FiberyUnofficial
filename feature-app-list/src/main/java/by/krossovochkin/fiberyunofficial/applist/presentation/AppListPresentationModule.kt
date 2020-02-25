package by.krossovochkin.fiberyunofficial.applist.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
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
        return ViewModelProvider(fragment, appListViewModelFactory).get()
    }

    @JvmStatic
    @Provides
    fun appListViewModelFactory(
        getAppListInteractor: GetAppListInteractor,
        appListParentListener: AppListViewModel.ParentListener
    ): AppListViewModelFactory {
        return AppListViewModelFactory(
            getAppListInteractor,
            appListParentListener
        )
    }
}
