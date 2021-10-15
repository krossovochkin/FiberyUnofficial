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
package com.krossovochkin.fiberyunofficial.di.pickerentity

import com.krossovochkin.fiberyunofficial.api.FiberyApiRepository
import com.krossovochkin.fiberyunofficial.api.FiberyServiceApi
import com.krossovochkin.fiberyunofficial.entitycreatedata.EntityCreateRepositoryImpl
import com.krossovochkin.fiberyunofficial.entitycreatedomain.EntityCreateRepository
import com.krossovochkin.fiberyunofficial.entitypicker.data.EntityPickerRepositoryImpl
import com.krossovochkin.fiberyunofficial.entitypicker.domain.EntityPickerRepository
import dagger.Module
import dagger.Provides

@Module
object EntityPickerDataModule {

    @JvmStatic
    @Provides
    fun entityPickerRepository(
        fiberyServiceApi: FiberyServiceApi,
        fiberyApiRepository: FiberyApiRepository
    ): EntityPickerRepository {
        return EntityPickerRepositoryImpl(
            fiberyServiceApi = fiberyServiceApi,
            fiberyApiRepository = fiberyApiRepository
        )
    }

    @JvmStatic
    @Provides
    fun entityCreateRepository(
        fiberyServiceApi: FiberyServiceApi
    ): EntityCreateRepository {
        return EntityCreateRepositoryImpl(
            fiberyServiceApi
        )
    }
}
