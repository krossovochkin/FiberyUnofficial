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
package com.krossovochkin.fiberyunofficial.di.entitydetails

import com.krossovochkin.fiberyunofficial.core.data.api.FiberyApiRepository
import com.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import com.krossovochkin.fiberyunofficial.entitydetails.data.EntityDetailsRepositoryImpl
import com.krossovochkin.fiberyunofficial.entitydetails.domain.EntityDetailsRepository
import dagger.Module
import dagger.Provides

@Module
object EntityDetailsDataModule {

    @JvmStatic
    @Provides
    fun entityDetailsRepository(
        fiberyServiceApi: FiberyServiceApi,
        fiberyApiRepository: FiberyApiRepository
    ): EntityDetailsRepository {
        return EntityDetailsRepositoryImpl(fiberyServiceApi, fiberyApiRepository)
    }
}