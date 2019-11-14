package by.krossovochkin.fiberyunofficial.applist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import by.krossovochkin.fiberyunofficial.applist.domain.GetAppListInteractor
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import kotlinx.coroutines.launch

class AppListViewModel(
    private val navController: NavController,
    private val getAppListInteractor: GetAppListInteractor
) : ViewModel() {

    private val mutableAppItems = MutableLiveData<List<ListItem>>()
    val appItems: LiveData<List<ListItem>> = mutableAppItems

    init {
        viewModelScope.launch {
            mutableAppItems.value = getAppListInteractor.execute()
                .map { app ->
                    AppListItem(
                        title = app.name,
                        appData = app
                    )
                }
        }
    }

    fun select(item: ListItem) {
        if (item is AppListItem) {
            navController.navigate(
                AppListFragmentDirections.actionAppListToEntityTypeList(
                    item.appData
                )
            )
        } else {
            throw IllegalArgumentException()
        }
    }
}
