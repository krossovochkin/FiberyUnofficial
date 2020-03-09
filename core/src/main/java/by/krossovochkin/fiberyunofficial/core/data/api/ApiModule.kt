package by.krossovochkin.fiberyunofficial.core.data.api

import by.krossovochkin.fiberyunofficial.core.data.api.mapper.FiberyEntityTypeMapper
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
object ApiModule {

    @Singleton
    @JvmStatic
    @Provides
    fun fiberyServiceApi(retrofit: Retrofit): FiberyServiceApi {
        return retrofit.create()
    }

    @Singleton
    @JvmStatic
    @Provides
    fun fiberyApiRepository(
        fiberyServiceApi: FiberyServiceApi,
        fiberyEntityTypeMapper: FiberyEntityTypeMapper
    ): FiberyApiRepository {
        return FiberyApiRepositoryImpl(
            fiberyServiceApi = fiberyServiceApi,
            fiberyEntityTypeMapper = fiberyEntityTypeMapper
        )
    }

    @Singleton
    @JvmStatic
    @Provides
    fun fiberyEntityTypeMapper(): FiberyEntityTypeMapper {
        return FiberyEntityTypeMapper()
    }
}
