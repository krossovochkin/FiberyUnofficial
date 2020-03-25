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
package by.krossovochkin.fiberyunofficial

import android.content.Context
import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import by.krossovochkin.fiberyunofficial.core.data.api.ApiModule
import by.krossovochkin.fiberyunofficial.core.data.auth.AuthStorage
import by.krossovochkin.fiberyunofficial.core.data.network.NetworkModule
import by.krossovochkin.fiberyunofficial.core.presentation.ResProvider
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, ApiModule::class, AuthModule::class])
interface ApplicationComponent : GlobalDependencies {

    fun authStorage(): AuthStorage

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): ApplicationComponent
    }
}

@Module
object AuthModule {

    @JvmStatic
    @Provides
    fun authStorage(
        context: Context
    ): AuthStorage {
        return AuthStorageImpl(context.applicationContext)
    }

    @JvmStatic
    @Provides
    fun resProvider(
        context: Context
    ): ResProvider {
        return ResProviderImpl(context.applicationContext)
    }
}
