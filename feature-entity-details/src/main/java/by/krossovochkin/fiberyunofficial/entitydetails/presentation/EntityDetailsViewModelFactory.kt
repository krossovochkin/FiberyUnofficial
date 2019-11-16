package by.krossovochkin.fiberyunofficial.entitydetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.krossovochkin.fiberyunofficial.entitydetails.domain.GetEntityDetailsInteractor
import java.lang.IllegalArgumentException

class EntityDetailsViewModelFactory(
    private val getEntityDetailsInteractor: GetEntityDetailsInteractor,
    private val entityDetailsParentListener: EntityDetailsViewModel.ParentListener,
    private val entityDetailsArgs: EntityDetailsFragment.Args
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return if (modelClass == EntityDetailsViewModel::class.java) {
            EntityDetailsViewModel(
                getEntityDetailsInteractor,
                entityDetailsParentListener,
                entityDetailsArgs
            ) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
