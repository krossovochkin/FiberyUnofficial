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
    val query: FiberyRequestCommandArgsQueryDto,
    val params: Map<String, Any>? = null
)

@JsonClass(generateAdapter = true)
data class FiberyRequestCommandArgsQueryDto(
    @Json(name = "q/from")
    val from: String,
    @Json(name = "q/select")
    val select: List<Any>,
    @Json(name = "q/where")
    val where: List<Any>? = null,
    @Json(name = "q/order-by")
    val orderBy: List<Any>? = null,
    @Json(name = "q/limit")
    val limit: Int
)

enum class FiberyCommand(val value: String) {
    QUERY_SCHEMA("fibery.schema/query"),
    QUERY_ENTITY("fibery.entity/query")
}
