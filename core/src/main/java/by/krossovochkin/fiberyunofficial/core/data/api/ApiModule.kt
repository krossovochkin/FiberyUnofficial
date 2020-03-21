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
