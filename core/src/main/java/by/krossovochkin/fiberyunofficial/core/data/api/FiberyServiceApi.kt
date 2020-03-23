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
package by.krossovochkin.fiberyunofficial.core.data.api

import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommand
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCreateCommandBody
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyDeleteCommandBody
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyDocumentResponse
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandBody
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyResponseDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyResponseEntityDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyUpdateCommandBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FiberyServiceApi {

    @POST("api/commands")
    suspend fun authenticate(
        @Body body: Any = Any()
    )

    @POST("api/commands")
    suspend fun getSchema(
        @Body body: List<FiberyRequestCommandBody> = listOf(
            FiberyRequestCommandBody(command = FiberyCommand.QUERY_SCHEMA.value)
        )
    ): List<FiberyResponseDto>

    @POST("api/commands")
    suspend fun getEntities(
        @Body body: List<FiberyRequestCommandBody>
    ): List<FiberyResponseEntityDto>

    @POST("api/commands")
    suspend fun updateEntity(
        @Body body: List<FiberyUpdateCommandBody>
    ): Any

    @POST("api/commands")
    suspend fun createEntity(
        @Body body: List<FiberyCreateCommandBody>
    ): Any

    @POST("api/commands")
    suspend fun deleteEntity(
        @Body body: List<FiberyDeleteCommandBody>
    ): Any

    @GET("api/documents/{secret}")
    fun getDocument(
        @Path("secret") secret: String,
        @Query("format") format: String = "md"
    ): Call<FiberyDocumentResponse?>
}
