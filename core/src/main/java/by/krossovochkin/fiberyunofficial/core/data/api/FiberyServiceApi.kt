package by.krossovochkin.fiberyunofficial.core.data.api

import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyCommand
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyRequestCommandBody
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyResponseDto
import by.krossovochkin.fiberyunofficial.core.data.api.dto.FiberyResponseEntityDto
import retrofit2.http.Body
import retrofit2.http.POST

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
}