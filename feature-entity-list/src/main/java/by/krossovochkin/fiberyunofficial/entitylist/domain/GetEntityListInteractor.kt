package by.krossovochkin.fiberyunofficial.entitylist.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema

interface GetEntityListInteractor {

    suspend fun execute(entityType: FiberyEntityTypeSchema): List<FiberyEntityData>
}

class GetEntityListInteractorImpl(
    private val entityListRepository: EntityListRepository
) : GetEntityListInteractor {

    override suspend fun execute(entityType: FiberyEntityTypeSchema): List<FiberyEntityData> {
        return entityListRepository.getEntityList(entityType)
    }
}
