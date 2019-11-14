package by.krossovochkin.fiberyunofficial.entitytypelist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.entitytypelist.domain.GetEntityTypeListInteractor

class EntityTypeListViewModelFactory(
    private val navController: NavController,
    private val getEntityTypeListInteractor: GetEntityTypeListInteractor,
    private val fiberyAppData: FiberyAppData
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == EntityTypeListViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            EntityTypeListViewModel(
                navController,
                getEntityTypeListInteractor,
                fiberyAppData
            ) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
