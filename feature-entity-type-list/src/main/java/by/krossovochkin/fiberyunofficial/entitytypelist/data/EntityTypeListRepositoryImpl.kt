package by.krossovochkin.fiberyunofficial.entitytypelist.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiConstants
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldMetaData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.entitytypelist.domain.EntityTypeListRepository

class EntityTypeListRepositoryImpl(
    private val fiberyServiceApi: FiberyServiceApi
) : EntityTypeListRepository {

    override suspend fun getEntityTypeList(appData: FiberyAppData): List<FiberyEntityTypeSchema> {
        val schema = fiberyServiceApi.getSchema(

        )
        val typesDto = schema.first()
            .result.fiberyTypes.filter { it.meta.isDomain && it.name != FiberyApiConstants.Type.USER.value }
        return typesDto
            .filter { typeDto ->
                typeDto.name.startsWith(appData.name)
            }
            .map { typeDto ->
                FiberyEntityTypeSchema(
                    name = typeDto.name,
                    fields = typeDto.fields.map { fieldDto ->
                        FiberyFieldSchema(
                            fieldDto.name,
                            fieldDto.type,
                            FiberyFieldMetaData(
                                isUiTitle = fieldDto.meta.isUiTitle ?: false,
                                isCollection = fieldDto.meta.isCollection ?: false,
                                isRelation = fieldDto.meta.relationId != null
                            )
                        )
                    },
                    uiColorHex = typeDto.meta.uiColorHex ?: DEFAULT_UI_COLOR
                )
            }
    }

    companion object {
        private const val DEFAULT_UI_COLOR = "#00FFFFFF"
    }
}
