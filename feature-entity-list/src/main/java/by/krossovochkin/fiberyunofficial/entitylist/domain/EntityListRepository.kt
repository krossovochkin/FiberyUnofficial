package by.krossovochkin.fiberyunofficial.entitylist.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema

interface EntityListRepository {

    suspend fun getEntityList(
        entityType: FiberyEntityTypeSchema,
        offset: Int,
        pageSize: Int,
        entityParams: Pair<FiberyFieldSchema, FiberyEntityData>?
    ): List<FiberyEntityData>
}
