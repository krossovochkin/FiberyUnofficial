package by.krossovochkin.fiberyunofficial.entitydetails.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData

interface UpdateMultiSelectFieldInteractor {

    suspend fun execute(
        entityData: FiberyEntityData,
        fieldSchema: FiberyFieldSchema,
        addedItems: List<FieldData.EnumItemData>,
        removedItems: List<FieldData.EnumItemData>
    )
}

class UpdateMultiSelectFieldInteractorImpl(
    private val entityDetailsRepository: EntityDetailsRepository
) : UpdateMultiSelectFieldInteractor {

    override suspend fun execute(
        entityData: FiberyEntityData,
        fieldSchema: FiberyFieldSchema,
        addedItems: List<FieldData.EnumItemData>,
        removedItems: List<FieldData.EnumItemData>
    ) {
        return entityDetailsRepository.updateMultiSelectField(
            entityData = entityData,
            fieldSchema = fieldSchema,
            addedItems = addedItems,
            removedItems = removedItems
        )
    }
}
