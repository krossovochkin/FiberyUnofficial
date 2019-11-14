package by.krossovochkin.fiberyunofficial.entitytypelist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.entitytypelist.domain.GetEntityTypeListInteractor
import by.krossovochkin.fiberyunofficial.utils.presentation.ColorUtils
import kotlinx.coroutines.launch

class EntityTypeListViewModel(
    private val navController: NavController,
    private val getEntityTypeListInteractor: GetEntityTypeListInteractor,
    private val fiberyAppData: FiberyAppData
) : ViewModel() {

    private val mutableEntityTypeItems = MutableLiveData<List<ListItem>>()
    val entityTypeItems: LiveData<List<ListItem>> = mutableEntityTypeItems

    init {
        viewModelScope.launch {
            mutableEntityTypeItems.value = getEntityTypeListInteractor.execute(fiberyAppData)
                .map { entityType ->
                    EntityTypeListItem(
                        title = entityType.displayName,
                        badgeBgColor = ColorUtils.getColor(entityType.uiColorHex),
                        entityTypeData = entityType
                    )
                }
        }
    }

    fun select(item: ListItem) {
        if (item is EntityTypeListItem) {
            navController.navigate(
                EntityTypeListFragmentDirections.actionEntityTypeListToEntityList(
                    item.entityTypeData
                )
            )
        } else {
            throw IllegalArgumentException()
        }
    }
}
