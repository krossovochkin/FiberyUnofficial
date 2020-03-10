package by.krossovochkin.fiberyunofficial.entitylist.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema

interface GetEntityListSortInteractor {

    fun execute(
        entityTypeSchema: FiberyEntityTypeSchema
    ): String
}

class GetEntityListSortInteractorImpl(
    private val entityListRepository: EntityListRepository
) : GetEntityListSortInteractor {

    override fun execute(entityTypeSchema: FiberyEntityTypeSchema): String {
        return entityListRepository.getEntityListSort(entityTypeSchema)
    }
}
