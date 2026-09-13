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
data class FiberyCommandBody(
    val command: String,
    val args: FiberyCommandArgsDto? = null
)

@Serializable
data class FiberyCommandArgsDto(
    val query: FiberyCommandArgsQueryDto? = null,
    val params: Map<String, @Contextual Any>? = null,
    val type: String? = null,
    val entity: Map<String, @Contextual Any?>? = null,
    val field: String? = null,
    val items: List<@Contextual Any>? = null
)

@Serializable
data class FiberyCommandArgsQueryDto(
    @SerialName("q/from")
    val from: String,
    @SerialName("q/select")
    val select: @Contextual Any,
    @SerialName("q/where")
    val where: List<@Contextual Any>? = null,
    @SerialName("q/order-by")
    val orderBy: List<@Contextual Any>? = null,
    @SerialName("q/offset")
    val offset: @Contextual Any? = null,
    @SerialName("q/limit")
    val limit: @Contextual Any? = null
)

enum class FiberyCommand(val value: String) {
    QUERY_SCHEMA("fibery.schema/query"),
    QUERY_ENTITY("fibery.entity/query"),
    QUERY_UPDATE("fibery.entity/update"),
    QUERY_CREATE("fibery.entity/create"),
    QUERY_DELETE("fibery.entity/delete"),
    QUERY_ADD_COLLECTION_ITEM("fibery.entity/add-collection-items"),
    QUERY_REMOVE_COLLECTION_ITEM("fibery.entity/remove-collection-items")
}
