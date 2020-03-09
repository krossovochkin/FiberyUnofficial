package by.krossovochkin.fiberyunofficial.entitycreate.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.krossovochkin.fiberyunofficial.entitycreate.domain.EntityCreateInteractor

class EntityCreateViewModelFactory(
    private val entityCreateArgs: EntityCreateFragment.Args,
    private val entityCreateInteractor: EntityCreateInteractor
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == EntityCreateViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            EntityCreateViewModel(
                entityCreateArgs,
                entityCreateInteractor
            ) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
