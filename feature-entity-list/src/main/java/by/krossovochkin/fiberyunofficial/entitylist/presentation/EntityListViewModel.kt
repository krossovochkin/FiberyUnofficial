package by.krossovochkin.fiberyunofficial.entitylist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListInteractor
import kotlinx.coroutines.launch

class EntityListViewModel(
    private val getEntityListInteractor: GetEntityListInteractor,
    private val entityListParentListener: ParentListener,
    private val entityListArgs: EntityListFragment.Args
) : ViewModel() {

    private val mutableEntityTypeItems = MutableLiveData<List<ListItem>>()
    val entityTypeItems: LiveData<List<ListItem>> = mutableEntityTypeItems

    val toolbarViewState: EntityListToolbarViewState
        get() = EntityListToolbarViewState(
            title = entityListArgs.entityTypeSchema.displayName,
            bgColorInt = ColorUtils.getColor(entityListArgs.entityTypeSchema.meta.uiColorHex)
        )

    init {
        viewModelScope.launch {
            mutableEntityTypeItems.value = getEntityListInteractor
                .execute(entityListArgs.entityTypeSchema, entityListArgs.entityParams)
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
            entityListParentListener.onEntitySelected(item.entityData)
        } else {
            throw IllegalArgumentException()
        }
    }

    interface ParentListener {

        fun onEntitySelected(entity: FiberyEntityData)
    }
}
