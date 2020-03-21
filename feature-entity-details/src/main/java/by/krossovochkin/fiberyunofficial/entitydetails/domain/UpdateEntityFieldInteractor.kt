package by.krossovochkin.fiberyunofficial.entitydetails.domain

import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema

interface UpdateEntityFieldInteractor {

    suspend fun execute(
        entityData: FiberyEntityData,
        fieldSchema: FiberyFieldSchema,
        selectedEntity: FiberyEntityData?
    )
}

class UpdateEntityFieldInteractorImpl(
    private val entityDetailsRepository: EntityDetailsRepository
) : UpdateEntityFieldInteractor {

    override suspend fun execute(
        entityData: FiberyEntityData,
        fieldSchema: FiberyFieldSchema,
        selectedEntity: FiberyEntityData?
    ) {
        entityDetailsRepository.updateEntityField(
            entityData = entityData,
            fieldSchema = fieldSchema,
            selectedEntity = selectedEntity
        )
    }
}
