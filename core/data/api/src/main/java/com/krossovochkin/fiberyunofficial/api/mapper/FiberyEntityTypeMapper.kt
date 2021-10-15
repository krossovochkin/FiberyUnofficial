/*
   Copyright 2020 Vasya Drobushkov

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package com.krossovochkin.fiberyunofficial.api.mapper

import com.krossovochkin.fiberyunofficial.api.dto.FiberyTypeDto
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeMetaData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldMetaData
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema

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
                        relationId = fieldDto.meta.relationId,
                        isCollection = fieldDto.meta.isCollection ?: false,
                        uiOrder = fieldDto.meta.uiOrder ?: 0,
                        numberUnit = fieldDto.meta.numberUnit,
                        numberPrecision = fieldDto.meta.numberPrecision ?: 0
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
