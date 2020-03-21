package by.krossovochkin.fiberyunofficial.entitypicker.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema

interface GetEntityListInteractor {

    suspend fun execute(
        fieldSchema: FiberyFieldSchema,
        offset: Int,
        pageSize: Int
    ): List<FiberyEntityData>
}

class GetEntityListInteractorImpl(
    private val entityPickerRepository: EntityPickerRepository
) : GetEntityListInteractor {

    override suspend fun execute(
        fieldSchema: FiberyFieldSchema,
        offset: Int,
        pageSize: Int
    ): List<FiberyEntityData> {
        return entityPickerRepository.getEntityList(fieldSchema, offset, pageSize)
    }
}
