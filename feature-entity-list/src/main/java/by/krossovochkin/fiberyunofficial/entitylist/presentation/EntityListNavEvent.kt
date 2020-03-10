package by.krossovochkin.fiberyunofficial.entitylist.presentation

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema

sealed class EntityListNavEvent {

    object BackEvent : EntityListNavEvent()

    data class OnEntitySelectedEvent(
        val entity: FiberyEntityData
    ) : EntityListNavEvent()

    data class OnFilterSelectedEvent(
        val filter: String,
        val params: String
    ) : EntityListNavEvent()

    data class OnSortSelectedEvent(
        val sort: String
    ) : EntityListNavEvent()

    data class OnCreateEntityEvent(
        val entityType: FiberyEntityTypeSchema
    ) : EntityListNavEvent()
}
