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

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FiberySchemaResponseDto(
    @Json(name = "success")
    val isSuccess: Boolean,
    @Json(name = "result")
    val result: FiberySchemaResultDto
)

@JsonClass(generateAdapter = true)
data class FiberySchemaResultDto(
    @Json(name = "fibery/types")
    val fiberyTypes: List<FiberyTypeDto>
)

@JsonClass(generateAdapter = true)
data class FiberyTypeDto(
    @Json(name = "fibery/name")
    val name: String,
    @Json(name = "fibery/meta")
    val meta: FiberyTypeMetaDto,
    @Json(name = "fibery/fields")
    val fields: List<FiberyFieldDto>
)

@JsonClass(generateAdapter = true)
data class FiberyFieldDto(
    @Json(name = "fibery/name")
    val name: String,
    @Json(name = "fibery/type")
    val type: String,
    @Json(name = "fibery/meta")
    val meta: FiberyFieldMetaDto
)

@JsonClass(generateAdapter = true)
data class FiberyFieldMetaDto(
    @Json(name = "ui/title?")
    val isUiTitle: Boolean?,
    @Json(name = "fibery/collection?")
    val isCollection: Boolean?,
    @Json(name = "fibery/relation")
    val relationId: String?,
    @Json(name = "ui/object-editor-order")
    val uiOrder: Int?,
    @Json(name = "ui/number-unit")
    val numberUnit: String?,
    @Json(name = "ui/number-precision")
    val numberPrecision: Int?
)

@JsonClass(generateAdapter = true)
data class FiberyTypeMetaDto(
    @Json(name = "fibery/domain?")
    val isDomain: Boolean?,
    @Json(name = "ui/color")
    val uiColorHex: String?,
    @Json(name = "fibery/primitive?")
    val isPrimitive: Boolean?,
    @Json(name = "fibery/enum?")
    val isEnum: Boolean?
)

@JsonClass(generateAdapter = true)
data class FiberyEntityResponseDto(
    @Json(name = "success")
    val isSuccess: Boolean,
    @Json(name = "result")
    val result: List<Map<String, Any>>
)

@JsonClass(generateAdapter = true)
data class FiberyDocumentResponse(
    @Json(name = "content")
    val content: String
)

@JsonClass(generateAdapter = true)
data class FiberyCreatedEntityResponseDto(
    @Json(name = "success")
    val isSuccess: Boolean,
    @Json(name = "result")
    val result: Result
) {

    @JsonClass(generateAdapter = true)
    data class Result(
        @Json(name = "fibery/id")
        val id: String
    )
}

@JsonClass(generateAdapter = true)
data class FiberyCommandResponseDto(
    @Json(name = "success")
    val isSuccess: Boolean
)

fun List<FiberyCommandResponseDto>.checkResultSuccess() {
    if (this.any { !it.isSuccess }) {
        throw RuntimeException()
    }
}
