package by.krossovochkin.fiberyunofficial.entitypicker.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema

interface EntityPickerRepository {

    suspend fun getEntityList(
        fieldSchema: FiberyFieldSchema,
        offset: Int,
        pageSize: Int
    ): List<FiberyEntityData>
}
