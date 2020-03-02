package by.krossovochkin.fiberyunofficial.entitytypelist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.krossovochkin.fiberyunofficial.entitytypelist.domain.GetEntityTypeListInteractor

class EntityTypeListViewModelFactory(
    private val getEntityTypeListInteractor: GetEntityTypeListInteractor,
    private val args: EntityTypeListFragment.Args
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == EntityTypeListViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            EntityTypeListViewModel(
                getEntityTypeListInteractor,
                args
            ) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
