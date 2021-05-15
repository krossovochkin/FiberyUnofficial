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
package com.krossovochkin.fiberyunofficial.core.data.api

import com.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommandBody
import com.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommandResponseDto
import com.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyDocumentResponse
import com.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyEntityResponseDto
import com.krossovochkin.fiberyunofficial.core.data.api.dto.FiberySchemaResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FiberyServiceApi {

    @POST("api/commands")
    suspend fun getSchema(
        @Body body: List<FiberyCommandBody>
    ): List<FiberySchemaResponseDto>

    @POST("api/commands")
    suspend fun getEntities(
        @Body body: List<FiberyCommandBody>
    ): List<FiberyEntityResponseDto>

    @POST("api/commands")
    suspend fun sendCommand(
        @Body body: List<FiberyCommandBody>
    ): List<FiberyCommandResponseDto>

    @GET("api/documents/{secret}")
    suspend fun getDocument(
        @Path("secret") secret: String,
        @Query("format") format: String = "md"
    ): FiberyDocumentResponse
}
