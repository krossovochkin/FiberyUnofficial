package by.krossovochkin.fiberyunofficial.applist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.krossovochkin.fiberyunofficial.applist.domain.GetAppListInteractor

class AppListViewModelFactory(
    private val getAppListInteractor: GetAppListInteractor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == AppListViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            AppListViewModel(
                getAppListInteractor
            ) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
