package by.krossovochkin.fiberyunofficial.entitydetails.presentation

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema

sealed class EntityDetailsNavEvent {

    object BackEvent : EntityDetailsNavEvent()

    data class OnEntitySelectedEvent(
        val entity: FiberyEntityData
    ) : EntityDetailsNavEvent()

    data class OnEntityFieldEditEvent(
        val fieldSchema: FiberyFieldSchema,
        val currentEntity: FiberyEntityData?
    ) : EntityDetailsNavEvent()

    data class OnEntityTypeSelectedEvent(
        val entityTypeSchema: FiberyEntityTypeSchema,
        val entity: FiberyEntityData,
        val fieldSchema: FiberyFieldSchema
    ) : EntityDetailsNavEvent()

    data class OnSingleSelectSelectedEvent(
        val singleSelectItem: FieldSingleSelectItem
    ) : EntityDetailsNavEvent()
}
