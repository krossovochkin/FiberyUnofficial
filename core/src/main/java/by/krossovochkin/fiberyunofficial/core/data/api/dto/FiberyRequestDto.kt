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
    @Json(name = "q/offset")
    val offset: Any? = null,
    @Json(name = "q/limit")
    val limit: Any? = null
)

@JsonClass(generateAdapter = true)
data class FiberyUpdateCommandBody(
    val command: String,
    val args: FiberyUpdateCommandArgsDto? = null
)

@JsonClass(generateAdapter = true)
data class FiberyUpdateCommandArgsDto(
    val type: String,
    val entity: Map<String, Any?>? = null
)

@JsonClass(generateAdapter = true)
data class FiberyCreateCommandBody(
    val command: String,
    val args: FiberyCreateCommandArgsDto? = null
)

@JsonClass(generateAdapter = true)
data class FiberyCreateCommandArgsDto(
    val type: String,
    val entity: Map<String, Any>? = null
)

enum class FiberyCommand(val value: String) {
    QUERY_SCHEMA("fibery.schema/query"),
    QUERY_ENTITY("fibery.entity/query"),
    QUERY_UPDATE("fibery.entity/update"),
    QUERY_CREATE("fibery.entity/create")
}
