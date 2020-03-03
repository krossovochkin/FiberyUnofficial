package by.krossovochkin.fiberyunofficial.entitylist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListFilterInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListSortInteractor

class EntityListViewModelFactory(
    private val getEntityListInteractor: GetEntityListInteractor,
    private val setEntityListFilterInteractor: SetEntityListFilterInteractor,
    private val setEntityListSortInteractor: SetEntityListSortInteractor,
    private val entityListArgs: EntityListFragment.Args
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == EntityListViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            EntityListViewModel(
                getEntityListInteractor,
                setEntityListFilterInteractor,
                setEntityListSortInteractor,
                entityListArgs
            ) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
