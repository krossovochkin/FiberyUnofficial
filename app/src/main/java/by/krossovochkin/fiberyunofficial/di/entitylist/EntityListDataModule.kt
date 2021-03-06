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
package by.krossovochkin.fiberyunofficial.di.entitylist

import android.content.Context
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiRepository
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.core.data.serialization.Serializer
import by.krossovochkin.fiberyunofficial.entitylist.data.EntityListFiltersSortStorage
import by.krossovochkin.fiberyunofficial.entitylist.data.EntityListFiltersSortStorageImpl
import by.krossovochkin.fiberyunofficial.entitylist.data.EntityListRepositoryImpl
import by.krossovochkin.fiberyunofficial.entitylist.domain.EntityListRepository
import dagger.Module
import dagger.Provides

@Module
object EntityListDataModule {

    @JvmStatic
    @Provides
    fun entityListRepository(
        fiberyServiceApi: FiberyServiceApi,
        fiberyApiRepository: FiberyApiRepository,
        entityListFiltersSortStorage: EntityListFiltersSortStorage
    ): EntityListRepository {
        return EntityListRepositoryImpl(
            fiberyServiceApi,
            fiberyApiRepository,
            entityListFiltersSortStorage
        )
    }

    @JvmStatic
    @Provides
    fun entityListFiltersStorage(
        context: Context,
        serializer: Serializer
    ): EntityListFiltersSortStorage {
        return EntityListFiltersSortStorageImpl(context, serializer)
    }
}
