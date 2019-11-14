package by.krossovochkin.fiberyunofficial.core.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FiberyRequestCommandBody(
    val command: String,
    val args: FiberyRequestCommandArgsDto? = null
)

@JsonClass(generateAdapter = true)
data class FiberyRequestCommandArgsDto(
    val query: FiberyRequestCommandArgsQueryDto
)

@JsonClass(generateAdapter = true)
data class FiberyRequestCommandArgsQueryDto(
    @Json(name = "q/from")
    val from: String,
    @Json(name = "q/select")
    val select: List<String>,
    @Json(name = "q/limit")
    val limit: Int
)

enum class FiberyCommand(val value: String) {
    QUERY_SCHEMA("fibery.schema/query"),
    QUERY_ENTITY("fibery.entity/query")
}
