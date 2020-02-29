package by.krossovochkin.fiberyunofficial.core.data.api

import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommand
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

    @GET("api/documents/{secret}")
    fun getDocument(
        @Path("secret") secret: String,
        @Query("format") format: String = "md"
    ): Call<FiberyDocumentResponse?>
}
