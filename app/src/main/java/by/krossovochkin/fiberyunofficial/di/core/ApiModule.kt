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
package by.krossovochkin.fiberyunofficial.di.core

import android.content.Context
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiRepository
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiRepositoryImpl
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.core.data.api.mapper.FiberyEntityTypeMapper
import by.krossovochkin.fiberyunofficial.core.data.serialization.Serializer
import by.krossovochkin.fiberyunofficial.serialization.MoshiSerializer
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
}
