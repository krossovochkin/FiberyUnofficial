package by.krossovochkin.fiberyunofficial.entitylist.presentation

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData

sealed class EntityListNavEvent {

    object BackEvent : EntityListNavEvent()

    data class OnEntitySelectedEvent(
        val entity: FiberyEntityData
    ) : EntityListNavEvent()
}
