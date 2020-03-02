package by.krossovochkin.fiberyunofficial.entitydetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.krossovochkin.fiberyunofficial.entitydetails.domain.GetEntityDetailsInteractor
import by.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateEntitySingleSelectInteractor
import java.lang.IllegalArgumentException

class EntityDetailsViewModelFactory(
    private val getEntityDetailsInteractor: GetEntityDetailsInteractor,
    private val updateEntitySingleSelectInteractor: UpdateEntitySingleSelectInteractor,
    private val entityDetailsArgs: EntityDetailsFragment.Args
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return if (modelClass == EntityDetailsViewModel::class.java) {
            EntityDetailsViewModel(
                getEntityDetailsInteractor,
                updateEntitySingleSelectInteractor,
                entityDetailsArgs
            ) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
