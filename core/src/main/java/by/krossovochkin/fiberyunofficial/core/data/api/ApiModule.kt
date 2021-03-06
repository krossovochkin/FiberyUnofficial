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

import android.content.Context
import by.krossovochkin.fiberyunofficial.addDebugNetworkInterceptor
import by.krossovochkin.fiberyunofficial.core.data.api.mapper.FiberyEntityTypeMapper
import by.krossovochkin.fiberyunofficial.core.data.auth.AuthStorage
import by.krossovochkin.fiberyunofficial.core.data.serialization.MoshiSerializer
import by.krossovochkin.fiberyunofficial.core.data.serialization.Serializer
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
object ApiModule {

    @Singleton
    @JvmStatic
    @Provides
    fun fiberyServiceApi(
        authStorage: AuthStorage
    ): FiberyServiceApi {
        return retrofit(
            okHttpClient = okHttpClient(
                authorizationInterceptor = authorizationInterceptor(
                    authStorage = authStorage
                )
            ),
            authStorage = authStorage
        ).create()
    }

    @Singleton
    @JvmStatic
    @Provides
    fun fiberyApiRepository(
        context: Context,
        serializer: Serializer,
        fiberyServiceApi: FiberyServiceApi
    ): FiberyApiRepository {
        return FiberyApiRepositoryImpl(
            fiberyServiceApi = fiberyServiceApi,
            fiberyEntityTypeMapper = FiberyEntityTypeMapper(),
            context = context,
            serializer = serializer
        )
    }

    @Singleton
    @JvmStatic
    @Provides
    fun serializer(): Serializer {
        return MoshiSerializer(Moshi.Builder().build())
    }

    private fun retrofit(
        okHttpClient: OkHttpClient,
        authStorage: AuthStorage
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://${authStorage.getAccount()}.fibery.io/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create().withNullSerialization())
            .build()
    }

    private fun okHttpClient(
        authorizationInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authorizationInterceptor)
            .addDebugNetworkInterceptor()
            .build()
    }

    private fun authorizationInterceptor(
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
