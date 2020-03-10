package by.krossovochkin.fiberyunofficial.entitylist.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema

interface GetEntityListFilterInteractor {

    suspend fun execute(
        entityTypeSchema: FiberyEntityTypeSchema
    ): Pair<String, String>
}

class GetEntityListFilterInteractorImpl(
    private val entityListRepository: EntityListRepository
) : GetEntityListFilterInteractor {

    override suspend fun execute(entityTypeSchema: FiberyEntityTypeSchema): Pair<String, String> {
        return entityListRepository.getEntityListFilter(entityTypeSchema)
    }
}
