package by.krossovochkin.fiberyunofficial.entitydetails.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData

interface UpdateSingleSelectFieldInteractor {

    suspend fun execute(
        entityData: FiberyEntityData,
        fieldSchema: FiberyFieldSchema,
        singleSelectItem: FieldData.SingleSelectItemData
    )
}

class UpdateSingleSelectFieldInteractorImpl(
    private val entityDetailsRepository: EntityDetailsRepository
) : UpdateSingleSelectFieldInteractor {

    override suspend fun execute(
        entityData: FiberyEntityData,
        fieldSchema: FiberyFieldSchema,
        singleSelectItem: FieldData.SingleSelectItemData
    ) {
        return entityDetailsRepository.updateSingleSelectField(entityData, fieldSchema, singleSelectItem)
    }
}
