package by.krossovochkin.fiberyunofficial.entitylist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListInteractor

class EntityListViewModelFactory(
    private val navController: NavController,
    private val getEntityListInteractor: GetEntityListInteractor,
    private val entityTypeData: FiberyEntityTypeSchema
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == EntityListViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            EntityListViewModel(
                navController,
                getEntityListInteractor,
                entityTypeData
            ) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
