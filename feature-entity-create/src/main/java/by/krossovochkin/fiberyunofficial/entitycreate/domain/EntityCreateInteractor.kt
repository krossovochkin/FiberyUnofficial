package by.krossovochkin.fiberyunofficial.entitycreate.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema

interface EntityCreateInteractor {

    suspend fun execute(
        entityTypeSchema: FiberyEntityTypeSchema,
        name: String
    )
}

class EntityCreateInteractorImpl(
    private val entityCreateRepository: EntityCreateRepository
) : EntityCreateInteractor {

    override suspend fun execute(entityTypeSchema: FiberyEntityTypeSchema, name: String) {
        entityCreateRepository.createEntity(entityTypeSchema, name)
    }
}
