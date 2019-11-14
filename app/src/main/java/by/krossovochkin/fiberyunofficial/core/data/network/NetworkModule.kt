package by.krossovochkin.fiberyunofficial.core.data.network

import by.krossovochkin.fiberyunofficial.app.Secrets
import com.facebook.stetho.okhttp3.StethoInterceptor
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
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://${Secrets.API_ACCOUNT}.fibery.io/")
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
    fun authorizationInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val newRequest = request.newBuilder()
                .addHeader("Authorization", "Token ${Secrets.API_TOKEN}")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(newRequest)
        }
    }
}
