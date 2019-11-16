package by.krossovochkin.fiberyunofficial.core.data.network

import com.facebook.stetho.okhttp3.StethoInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
object NetworkModule {

    @Singleton
    @JvmStatic
    @Provides
    fun retrofit(
        @ApiAccount apiAccount: String,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://$apiAccount.fibery.io/")
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
            .addNetworkInterceptor(StethoInterceptor())
            .build()
    }

    @Singleton
    @JvmStatic
    @Provides
    fun authorizationInterceptor(
        @ApiToken apiToken: String
    ): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val newRequest = request.newBuilder()
                .addHeader("Authorization", "Token $apiToken")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(newRequest)
        }
    }
}

@Qualifier
@Retention
annotation class ApiAccount

@Qualifier
@Retention
annotation class ApiToken
