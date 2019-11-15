package by.krossovochkin.fiberyunofficial.entitylist.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema

interface EntityListRepository {

    suspend fun getEntityList(entityType: FiberyEntityTypeSchema): List<FiberyEntityData>
}
