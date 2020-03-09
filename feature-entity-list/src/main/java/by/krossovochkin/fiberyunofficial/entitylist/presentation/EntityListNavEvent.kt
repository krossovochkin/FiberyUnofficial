package by.krossovochkin.fiberyunofficial.entitylist.presentation

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema

sealed class EntityListNavEvent {

    object BackEvent : EntityListNavEvent()

    data class OnEntitySelectedEvent(
        val entity: FiberyEntityData
    ) : EntityListNavEvent()

    object OnFilterSelectedEvent : EntityListNavEvent()

    object OnSortSelectedEvent : EntityListNavEvent()

    data class OnCreateEntityEvent(
        val entityType: FiberyEntityTypeSchema
    ) : EntityListNavEvent()
}
