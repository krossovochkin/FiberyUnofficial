package by.krossovochkin.fiberyunofficial.entitytypelist.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiConstants
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiRepository
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.entitytypelist.domain.EntityTypeListRepository

class EntityTypeListRepositoryImpl(
    private val fiberyApiRepository: FiberyApiRepository
) : EntityTypeListRepository {

    override suspend fun getEntityTypeList(appData: FiberyAppData): List<FiberyEntityTypeSchema> {
        val typesDto = fiberyApiRepository.getTypeSchemas()
            .filter { it.meta.isDomain && it.name != FiberyApiConstants.Type.USER.value }
        return typesDto
            .filter { typeDto ->
                typeDto.name.startsWith(appData.name)
            }
    }
}
