package by.krossovochkin.fiberyunofficial.entitydetails.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.presentation.Event

class EntityPickedViewModel : ViewModel() {

    private val mutablePickedEntity =
        MutableLiveData<Event<Pair<FiberyFieldSchema, FiberyEntityData?>>>()
    val pickedEntity: LiveData<Event<Pair<FiberyFieldSchema, FiberyEntityData?>>> =
        mutablePickedEntity

    fun pickEntity(fieldSchema: FiberyFieldSchema, entityData: FiberyEntityData?) {
        mutablePickedEntity.value = Event(fieldSchema to entityData)
    }
}
