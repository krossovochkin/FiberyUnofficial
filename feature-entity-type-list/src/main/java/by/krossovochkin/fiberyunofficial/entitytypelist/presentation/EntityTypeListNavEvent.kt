package by.krossovochkin.fiberyunofficial.entitytypelist.presentation

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema

sealed class EntityTypeListNavEvent {

    object BackEvent : EntityTypeListNavEvent()

    data class OnEntityTypeSelectedEvent(
        val entityTypeSchema: FiberyEntityTypeSchema
    ) : EntityTypeListNavEvent()
}
