package by.krossovochkin.fiberyunofficial.core.data.api

import android.content.Context
import by.krossovochkin.fiberyunofficial.core.data.api.mapper.FiberyEntityTypeMapper
import com.squareup.moshi.Moshi
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
        context: Context,
        moshi: Moshi,
        fiberyServiceApi: FiberyServiceApi,
        fiberyEntityTypeMapper: FiberyEntityTypeMapper
    ): FiberyApiRepository {
        return FiberyApiRepositoryImpl(
            fiberyServiceApi = fiberyServiceApi,
            fiberyEntityTypeMapper = fiberyEntityTypeMapper,
            context = context,
            moshi = moshi
        )
    }

    @Singleton
    @JvmStatic
    @Provides
    fun fiberyEntityTypeMapper(): FiberyEntityTypeMapper {
        return FiberyEntityTypeMapper()
    }

    @Singleton
    @JvmStatic
    @Provides
    fun moshi(): Moshi {
        return Moshi.Builder().build()
    }
}
