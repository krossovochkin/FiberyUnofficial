package by.krossovochkin.fiberyunofficial.entitycreate.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema

interface EntityCreateRepository {

    suspend fun createEntity(
        entityTypeSchema: FiberyEntityTypeSchema,
        name: String,
        entityParams: Pair<FiberyFieldSchema, FiberyEntityData>?
    )
}
