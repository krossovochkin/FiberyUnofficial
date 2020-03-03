package by.krossovochkin.fiberyunofficial.entitylist.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema

interface SetEntityListSortInteractor {

    suspend fun execute(
        entityType: FiberyEntityTypeSchema,
        sort: String
    )
}

class SetEntityListSortInteractorImpl(
    private val entityListRepository: EntityListRepository
) : SetEntityListSortInteractor {

    override suspend fun execute(
        entityType: FiberyEntityTypeSchema,
        sort: String
    ) {
        entityListRepository.setEntityListSort(entityType, sort)
    }
}
