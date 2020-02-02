package by.krossovochkin.fiberyunofficial.core.data.api.mapper

import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyTypeDto
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeMetaData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldMetaData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema

class FiberyEntityTypeMapper {

    fun map(typeDto: FiberyTypeDto): FiberyEntityTypeSchema {
        return FiberyEntityTypeSchema(
            name = typeDto.name,
            fields = typeDto.fields.map { fieldDto ->
                FiberyFieldSchema(
                    fieldDto.name,
                    fieldDto.type,
                    FiberyFieldMetaData(
                        isUiTitle = fieldDto.meta.isUiTitle ?: false,
                        isRelation = fieldDto.meta.relationId != null,
                        isCollection = fieldDto.meta.isCollection ?: false
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

    companion object {
        private const val DEFAULT_UI_COLOR = "#00FFFFFF"
    }
}