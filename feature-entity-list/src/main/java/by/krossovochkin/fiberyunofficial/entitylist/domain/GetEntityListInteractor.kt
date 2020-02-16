package by.krossovochkin.fiberyunofficial.entitylist.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema

interface GetEntityListInteractor {

    suspend fun execute(
        entityType: FiberyEntityTypeSchema,
        offset: Int,
        pageSize: Int,
        entityParams: Pair<FiberyFieldSchema, FiberyEntityData>?
    ): List<FiberyEntityData>
}

class GetEntityListInteractorImpl(
    private val entityListRepository: EntityListRepository
) : GetEntityListInteractor {

    override suspend fun execute(
        entityType: FiberyEntityTypeSchema,
        offset: Int,
        pageSize: Int,
        entityParams: Pair<FiberyFieldSchema, FiberyEntityData>?
    ): List<FiberyEntityData> {
        return entityListRepository.getEntityList(entityType, offset, pageSize, entityParams)
    }
}
