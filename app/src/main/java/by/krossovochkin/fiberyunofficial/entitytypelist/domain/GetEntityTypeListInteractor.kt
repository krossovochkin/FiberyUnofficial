package by.krossovochkin.fiberyunofficial.entitytypelist.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema

interface GetEntityTypeListInteractor {

    suspend fun execute(appData: FiberyAppData): List<FiberyEntityTypeSchema>
}

class GetEntityTypeListInteractorImpl(
    private val entityTypeListRepository: EntityTypeListRepository
) : GetEntityTypeListInteractor {

    override suspend fun execute(appData: FiberyAppData): List<FiberyEntityTypeSchema> {
        return entityTypeListRepository.getEntityTypeList(appData)
    }
}
