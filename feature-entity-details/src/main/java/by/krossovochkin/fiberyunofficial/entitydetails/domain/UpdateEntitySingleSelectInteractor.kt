package by.krossovochkin.fiberyunofficial.entitydetails.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData

interface UpdateEntitySingleSelectInteractor {

    suspend fun execute(
        entityData: FiberyEntityData,
        fieldSchema: FiberyFieldSchema,
        singleSelectItem: FieldData.SingleSelectItemData
    )
}

class UpdateEntitySingleSelectInteractorImpl(
    private val entityDetailsRepository: EntityDetailsRepository
) : UpdateEntitySingleSelectInteractor {

    override suspend fun execute(
        entityData: FiberyEntityData,
        fieldSchema: FiberyFieldSchema,
        singleSelectItem: FieldData.SingleSelectItemData
    ) {
        return entityDetailsRepository.updateSingleSelect(entityData, fieldSchema, singleSelectItem)
    }
}
