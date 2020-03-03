package by.krossovochkin.fiberyunofficial.entitylist.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema

interface SetEntityListFilterInteractor {

    suspend fun execute(
        entityType: FiberyEntityTypeSchema,
        filter: String,
        params: String
    )
}

class SetEntityListFilterInteractorImpl(
    private val entityListRepository: EntityListRepository
) : SetEntityListFilterInteractor {

    override suspend fun execute(
        entityType: FiberyEntityTypeSchema,
        filter: String,
        params: String
    ) {
        entityListRepository.setEntityListFilter(entityType, filter, params)
    }
}
