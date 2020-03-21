package by.krossovochkin.fiberyunofficial.entitypicker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.krossovochkin.fiberyunofficial.entitypicker.domain.GetEntityListInteractor
import by.krossovochkin.fiberyunofficial.entitypicker.domain.GetEntityTypeSchemaInteractor

class EntityPickerViewModelFactory(
    private val getEntityTypeSchemaInteractor: GetEntityTypeSchemaInteractor,
    private val getEntityListInteractor: GetEntityListInteractor,
    private val entityPickerArgs: EntityPickerFragment.Args
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == EntityPickerViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            EntityPickerViewModel(
                getEntityTypeSchemaInteractor,
                getEntityListInteractor,
                entityPickerArgs
            ) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
