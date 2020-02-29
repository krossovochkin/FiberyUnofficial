package by.krossovochkin.fiberyunofficial.core.data.network

import by.krossovochkin.fiberyunofficial.core.data.auth.AuthStorage
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
object NetworkModule {

    @Singleton
    @JvmStatic
    @Provides
    fun retrofit(
        okHttpClient: OkHttpClient,
        authStorage: AuthStorage
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://${authStorage.getAccount()}.fibery.io/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Singleton
    @JvmStatic
    @Provides
    fun okHttpClient(
        authorizationInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authorizationInterceptor)
            .build()
    }

    @Singleton
    @JvmStatic
    @Provides
    fun authorizationInterceptor(
        authStorage: AuthStorage
    ): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val newRequest = request.newBuilder()
                .addHeader("Authorization", "Token ${authStorage.getToken()}")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(newRequest)
        }
    }
}
