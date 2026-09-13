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
package com.krossovochkin.fiberyunofficial.api.dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FiberySchemaResponseDto(
    @SerialName("success")
    val isSuccess: Boolean,
    @SerialName("result")
    val result: FiberySchemaResultDto
)

@Serializable
data class FiberySchemaResultDto(
    @SerialName("fibery/types")
    val fiberyTypes: List<FiberyTypeDto>
)

@Serializable
data class FiberyTypeDto(
    @SerialName("fibery/name")
    val name: String,
    @SerialName("fibery/meta")
    val meta: FiberyTypeMetaDto,
    @SerialName("fibery/fields")
    val fields: List<FiberyFieldDto>
)

@Serializable
data class FiberyFieldDto(
    @SerialName("fibery/name")
    val name: String,
    @SerialName("fibery/type")
    val type: String,
    @SerialName("fibery/meta")
    val meta: FiberyFieldMetaDto
)

@Serializable
data class FiberyFieldMetaDto(
    @SerialName("ui/title?")
    val isUiTitle: Boolean? = null,
    @SerialName("fibery/collection?")
    val isCollection: Boolean? = null,
    @SerialName("fibery/relation")
    val relationId: String? = null,
    @SerialName("ui/object-editor-order")
    val uiOrder: Int? = null,
    @SerialName("ui/number-unit")
    val numberUnit: String? = null,
    @SerialName("ui/number-precision")
    val numberPrecision: Int? = null
)

@Serializable
data class FiberyTypeMetaDto(
    @SerialName("fibery/domain?")
    val isDomain: Boolean? = null,
    @SerialName("ui/color")
    val uiColorHex: String? = null,
    @SerialName("fibery/primitive?")
    val isPrimitive: Boolean? = null,
    @SerialName("fibery/enum?")
    val isEnum: Boolean? = null
)

@Serializable
data class FiberyEntityResponseDto(
    @SerialName("success")
    val isSuccess: Boolean,
    @SerialName("result")
    val result: List<Map<String, @Contextual Any>>
)

@Serializable
data class FiberyDocumentResponse(
    @SerialName("content")
    val content: String
)

@Serializable
data class FiberyCreatedEntityResponseDto(
    @SerialName("success")
    val isSuccess: Boolean,
    @SerialName("result")
    val result: Result
) {

    @Serializable
    data class Result(
        @SerialName("fibery/id")
        val id: String
    )
}

@Serializable
data class FiberyCommandResponseDto(
    @SerialName("success")
    val isSuccess: Boolean
)

fun List<FiberyCommandResponseDto>.checkResultSuccess() {
    if (this.any { !it.isSuccess }) {
        throw RuntimeException()
    }
}
