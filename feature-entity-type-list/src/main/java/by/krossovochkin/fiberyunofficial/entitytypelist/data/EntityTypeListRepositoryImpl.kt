package by.krossovochkin.fiberyunofficial.entitytypelist.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiConstants
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.core.domain.*
import by.krossovochkin.fiberyunofficial.entitytypelist.domain.EntityTypeListRepository

class EntityTypeListRepositoryImpl(
    private val fiberyServiceApi: FiberyServiceApi
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
            .map { typeDto ->
                FiberyEntityTypeSchema(
                    name = typeDto.name,
                    fields = typeDto.fields.map { fieldDto ->
                        FiberyFieldSchema(
                            fieldDto.name,
                            fieldDto.type,
                            FiberyFieldMetaData(
                                isUiTitle = fieldDto.meta.isUiTitle ?: false
                            )
                        )
                    },
                    meta = FiberyEntityTypeMetaData(
                        uiColorHex = typeDto.meta.uiColorHex ?: DEFAULT_UI_COLOR,
                        isDomain = typeDto.meta.isDomain ?: false,
                        isPrimitive = typeDto.meta.isPrimitive ?: false,
                        isEnum = typeDto.meta.isEnum ?: false
                    )

                )
            }
    }

    companion object {
        private const val DEFAULT_UI_COLOR = "#00FFFFFF"
    }
}
