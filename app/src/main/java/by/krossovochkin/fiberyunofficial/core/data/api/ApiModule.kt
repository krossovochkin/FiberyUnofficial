package by.krossovochkin.fiberyunofficial.core.data.api

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
}
