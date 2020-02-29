package by.krossovochkin.fiberyunofficial.entitydetails.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityDetailsData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData

interface EntityDetailsRepository {

    suspend fun getEntityDetails(entityData: FiberyEntityData): FiberyEntityDetailsData

    suspend fun updateSingleSelect(
        entityData: FiberyEntityData,
        fieldSchema: FiberyFieldSchema,
        singleSelectItem: FieldData.SingleSelectItemData
    )
}
