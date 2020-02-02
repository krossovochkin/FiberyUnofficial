package by.krossovochkin.fiberyunofficial.entitytypelist.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiConstants
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.core.data.api.mapper.FiberyEntityTypeMapper
import by.krossovochkin.fiberyunofficial.core.domain.*
import by.krossovochkin.fiberyunofficial.entitytypelist.domain.EntityTypeListRepository

class EntityTypeListRepositoryImpl(
    private val fiberyServiceApi: FiberyServiceApi,
    private val entityTypeMapper: FiberyEntityTypeMapper = FiberyEntityTypeMapper()
) : EntityTypeListRepository {

    override suspend fun getEntityTypeList(appData: FiberyAppData): List<FiberyEntityTypeSchema> {
        val schema = fiberyServiceApi.getSchema(

        )
        val typesDto = schema.first()
            .result.fiberyTypes
            .filter { it.meta.isDomain == true && it.name != FiberyApiConstants.Type.USER.value }
        return typesDto
            .filter { typeDto ->
                typeDto.name.startsWith(appData.name)
            }
            .map(entityTypeMapper::map)
    }
}
