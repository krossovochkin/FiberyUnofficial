package by.krossovochkin.fiberyunofficial.entitycreate.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema

interface EntityCreateInteractor {

    suspend fun execute(
        entityTypeSchema: FiberyEntityTypeSchema,
        name: String,
        entityParams: Pair<FiberyFieldSchema, FiberyEntityData>?
    )
}

class EntityCreateInteractorImpl(
    private val entityCreateRepository: EntityCreateRepository
) : EntityCreateInteractor {

    override suspend fun execute(
        entityTypeSchema: FiberyEntityTypeSchema,
        name: String,
        entityParams: Pair<FiberyFieldSchema, FiberyEntityData>?
    ) {
        entityCreateRepository.createEntity(entityTypeSchema, name, entityParams)
    }
}
