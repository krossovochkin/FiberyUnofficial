package by.krossovochkin.fiberyunofficial.core.data.api

import by.krossovochkin.fiberyunofficial.core.data.api.dto.*
import retrofit2.http.*

interface FiberyServiceApi {

    @POST("api/commands")
    suspend fun authenticate(
        @Body body: Any = Any()
    )

    @POST("api/commands")
    suspend fun getSchema(
        @Body body: List<FiberyRequestCommandBody> = listOf(FiberyRequestCommandBody(command = FiberyCommand.QUERY_SCHEMA.value))
    ): List<FiberyResponseDto>

    @POST("api/commands")
    suspend fun getEntities(
        @Body body: List<FiberyRequestCommandBody>
    ): List<FiberyResponseEntityDto>

    @GET("api/documents/{secret}")
    suspend fun getDocument(
        @Path("secret") secret: String,
        @Query("format") format: String = "md"
    ): FiberyDocumentResponse
}
