package by.krossovochkin.fiberyunofficial.entitydetails.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityDetailsData

interface EntityDetailsRepository {

    suspend fun getEntityDetails(entityData: FiberyEntityData): FiberyEntityDetailsData
}
