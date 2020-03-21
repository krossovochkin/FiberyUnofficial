package by.krossovochkin.fiberyunofficial.entitypicker.domain

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiRepository
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema

interface GetEntityTypeSchemaInteractor {

    suspend fun execute(fieldSchema: FiberyFieldSchema): FiberyEntityTypeSchema
}

class GetEntityTypeSchemaInteractorImpl(
    private val fiberyApiRepository: FiberyApiRepository
) : GetEntityTypeSchemaInteractor {

    override suspend fun execute(fieldSchema: FiberyFieldSchema): FiberyEntityTypeSchema {
        return fiberyApiRepository.getTypeSchema(fieldSchema.type)
    }
}
