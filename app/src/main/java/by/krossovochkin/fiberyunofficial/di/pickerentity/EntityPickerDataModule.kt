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
package by.krossovochkin.fiberyunofficial.di.pickerentity

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiRepository
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.core.data.entitycreate.EntityCreateRepositoryImpl
import by.krossovochkin.fiberyunofficial.core.domain.entitycreate.EntityCreateRepository
import by.krossovochkin.fiberyunofficial.entitypicker.data.EntityPickerRepositoryImpl
import by.krossovochkin.fiberyunofficial.entitypicker.domain.EntityPickerRepository
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
