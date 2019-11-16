package by.krossovochkin.fiberyunofficial.entitydetails.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityDetailsData

interface GetEntityDetailsInteractor {

    suspend fun execute(entityData: FiberyEntityData): FiberyEntityDetailsData
}

class GetEntityDetailsInteractorImpl(
    private val entityDetailsRepository: EntityDetailsRepository
) : GetEntityDetailsInteractor {

    override suspend fun execute(entityData: FiberyEntityData): FiberyEntityDetailsData {
        return entityDetailsRepository.getEntityDetails(entityData)
    }
}
