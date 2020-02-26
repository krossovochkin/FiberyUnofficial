package by.krossovochkin.fiberyunofficial.entitytypelist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.Event
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.entitytypelist.domain.GetEntityTypeListInteractor
import kotlinx.coroutines.launch

class EntityTypeListViewModel(
    private val getEntityTypeListInteractor: GetEntityTypeListInteractor,
    private val entityTypeListParentListener: ParentListener,
    private val args: EntityTypeListFragment.Args
) : ViewModel() {

    private val mutableEntityTypeItems = MutableLiveData<List<ListItem>>()
    val entityTypeItems: LiveData<List<ListItem>> = mutableEntityTypeItems

    private val mutableProgress = MutableLiveData<Boolean>()
    val progress: LiveData<Boolean> = mutableProgress

    private val mutableError = MutableLiveData<Event<Exception>>()
    val error: LiveData<Event<Exception>> = mutableError

    init {
        viewModelScope.launch {
            try {
                mutableProgress.value = true
                mutableEntityTypeItems.value =
                    getEntityTypeListInteractor.execute(args.fiberyAppData)
                        .map { entityType ->
                            EntityTypeListItem(
                                title = entityType.displayName,
                                badgeBgColor = ColorUtils.getColor(entityType.meta.uiColorHex),
                                entityTypeData = entityType
                            )
                        }
            } catch (e: Exception) {
                mutableError.value = Event(e)
            } finally {
                mutableProgress.value = false
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

    fun onBackPressed() {
        entityTypeListParentListener.onBackPressed()
    }

    interface ParentListener {

        fun onEntityTypeSelected(entityTypeSchema: FiberyEntityTypeSchema)

        fun onBackPressed()
    }
}
