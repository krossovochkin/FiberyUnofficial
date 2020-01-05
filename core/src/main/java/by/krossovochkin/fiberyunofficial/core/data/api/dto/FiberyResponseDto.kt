package by.krossovochkin.fiberyunofficial.core.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FiberyResponseDto(
    @Json(name = "result")
    val result: FiberyResultDto
)

@JsonClass(generateAdapter = true)
data class FiberyResultDto(
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
    val relationId: String?
)

@JsonClass(generateAdapter = true)
data class FiberyTypeMetaDto(
    @Json(name = "fibery/domain?")
    val isDomain: Boolean?,
    @Json(name = "ui/color")
    val uiColorHex: String?,
    @Json(name = "fibery/primitive?")
    val isPrimitive: Boolean?
)

@JsonClass(generateAdapter = true)
data class FiberyResponseEntityDto(
    @Json(name = "result")
    val result: List<Map<String, Any>>
)
