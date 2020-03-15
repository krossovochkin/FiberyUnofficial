package by.krossovochkin.fiberyunofficial.entitycreate.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.Event
import by.krossovochkin.fiberyunofficial.entitycreate.domain.EntityCreateInteractor
import kotlinx.coroutines.launch

class EntityCreateViewModel(
    private val entityCreateArgs: EntityCreateFragment.Args,
    private val entityCreateInteractor: EntityCreateInteractor
) : ViewModel() {

    private val mutableNavigation = MutableLiveData<Event<EntityCreateNavEvent>>()
    val navigation: LiveData<Event<EntityCreateNavEvent>> = mutableNavigation

    private val mutableError = MutableLiveData<Event<Exception>>()
    val error: LiveData<Event<Exception>> = mutableError

    val toolbarViewState: EntityCreateToolbarViewState
        get() = EntityCreateToolbarViewState(
            title = entityCreateArgs.entityTypeSchema.displayName,
            bgColorInt = ColorUtils.getColor(entityCreateArgs.entityTypeSchema.meta.uiColorHex)
        )

    fun createEntity(name: String) {
        viewModelScope.launch {
            try {
                entityCreateInteractor
                    .execute(entityCreateArgs.entityTypeSchema, name, entityCreateArgs.entityParams)
                mutableNavigation.postValue(
                    Event(EntityCreateNavEvent.OnEntityCreateSuccessEvent)
                )
            } catch (e: Exception) {
                mutableError.postValue(Event(e))
            }
        }
    }
}
