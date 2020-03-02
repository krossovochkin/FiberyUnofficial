package by.krossovochkin.fiberyunofficial.entitylist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListInteractor

class EntityListViewModelFactory(
    private val getEntityListInteractor: GetEntityListInteractor,
    private val entityListArgs: EntityListFragment.Args
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == EntityListViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            EntityListViewModel(
                getEntityListInteractor,
                entityListArgs
            ) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
