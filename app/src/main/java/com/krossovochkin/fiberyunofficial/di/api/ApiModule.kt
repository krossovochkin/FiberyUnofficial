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
package com.krossovochkin.fiberyunofficial.di.api

import android.content.Context
import com.krossovochkin.auth.AuthStorage
import com.krossovochkin.fiberyunofficial.addDebugNetworkInterceptor
import com.krossovochkin.fiberyunofficial.api.FiberyApiRepository
import com.krossovochkin.fiberyunofficial.api.FiberyApiRepositoryImpl
import com.krossovochkin.fiberyunofficial.api.FiberyServiceApi
import com.krossovochkin.fiberyunofficial.api.mapper.FiberyEntityTypeMapper
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
        retrofit: Retrofit
    ): FiberyServiceApi {
        return retrofit.create()
    }

    @Singleton
    @JvmStatic
    @Provides
    fun fiberyApiRepository(
        context: Context,
        serializer: com.krossovochkin.serialization.Serializer,
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
    fun serializer(): com.krossovochkin.serialization.Serializer {
        return com.krossovochkin.serialization.MoshiSerializer(Moshi.Builder().build())
    }

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
            .addConverterFactory(MoshiConverterFactory.create().withNullSerialization())
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
            .addDebugNetworkInterceptor()
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
