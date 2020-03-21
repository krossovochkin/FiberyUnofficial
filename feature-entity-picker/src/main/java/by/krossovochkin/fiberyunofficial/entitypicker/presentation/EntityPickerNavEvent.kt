package by.krossovochkin.fiberyunofficial.entitypicker.presentation

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema

sealed class EntityPickerNavEvent {

    object BackEvent : EntityPickerNavEvent()

    data class OnEntityPickedEvent(
        val fieldSchema: FiberyFieldSchema,
        val entity: FiberyEntityData?
    ) : EntityPickerNavEvent()
}
