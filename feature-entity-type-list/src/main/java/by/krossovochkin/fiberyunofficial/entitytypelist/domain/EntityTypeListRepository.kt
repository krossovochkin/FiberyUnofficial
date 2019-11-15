package by.krossovochkin.fiberyunofficial.entitytypelist.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema

interface EntityTypeListRepository {

    suspend fun getEntityTypeList(appData: FiberyAppData): List<FiberyEntityTypeSchema>
}
