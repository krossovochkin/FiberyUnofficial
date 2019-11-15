package by.krossovochkin.fiberyunofficial.entitylist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.krossovochkin.fiberyunofficial.entitylist.EntityListArgs
import by.krossovochkin.fiberyunofficial.entitylist.EntityListParentListener
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListInteractor

class EntityListViewModelFactory(
    private val getEntityListInteractor: GetEntityListInteractor,
    private val entityListParentListener: EntityListParentListener,
    private val entityListArgs: EntityListArgs
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == EntityListViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            EntityListViewModel(
                getEntityListInteractor,
                entityListParentListener,
                entityListArgs
            ) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
