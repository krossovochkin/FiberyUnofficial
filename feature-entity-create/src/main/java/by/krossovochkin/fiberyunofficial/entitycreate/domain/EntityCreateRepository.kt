package by.krossovochkin.fiberyunofficial.entitycreate.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema

interface EntityCreateRepository {

    suspend fun createEntity(entityTypeSchema: FiberyEntityTypeSchema, name: String)
}
