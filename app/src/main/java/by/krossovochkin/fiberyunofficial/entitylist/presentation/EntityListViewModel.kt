package by.krossovochkin.fiberyunofficial.entitylist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListInteractor
import kotlinx.coroutines.launch

class EntityListViewModel(
    private val navController: NavController,
    private val getEntityListInteractor: GetEntityListInteractor,
    private val entityTypeData: FiberyEntityTypeSchema
) : ViewModel() {

    private val mutableEntityTypeItems = MutableLiveData<List<ListItem>>()
    val entityTypeItems: LiveData<List<ListItem>> = mutableEntityTypeItems

    init {
        viewModelScope.launch {
            mutableEntityTypeItems.value = getEntityListInteractor.execute(entityTypeData)
                .map { entity ->
                    EntityListItem(
                        title = entity.title,
                        entityData = entity
                    )
                }
        }
    }

    fun select(item: ListItem) {
        if (item is EntityListItem) {
            navController.navigate(
                EntityListFragmentDirections.actionEntityListToEntityDetails(
                    item.entityData
                )
            )
        } else {
            throw IllegalArgumentException()
        }
    }
}
