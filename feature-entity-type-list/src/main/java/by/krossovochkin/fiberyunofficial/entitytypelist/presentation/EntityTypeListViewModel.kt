package by.krossovochkin.fiberyunofficial.entitytypelist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.entitytypelist.EntityTypeListArgs
import by.krossovochkin.fiberyunofficial.entitytypelist.EntityTypeListParentListener
import by.krossovochkin.fiberyunofficial.entitytypelist.domain.GetEntityTypeListInteractor
import kotlinx.coroutines.launch

class EntityTypeListViewModel(
    private val getEntityTypeListInteractor: GetEntityTypeListInteractor,
    private val entityTypeListParentListener: EntityTypeListParentListener,
    private val args: EntityTypeListArgs
) : ViewModel() {

    private val mutableEntityTypeItems = MutableLiveData<List<ListItem>>()
    val entityTypeItems: LiveData<List<ListItem>> = mutableEntityTypeItems

    init {
        viewModelScope.launch {
            mutableEntityTypeItems.value = getEntityTypeListInteractor.execute(args.fiberyAppData)
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
            entityTypeListParentListener.onEntityTypeSelected(item.entityTypeData)
        } else {
            throw IllegalArgumentException()
        }
    }
}
