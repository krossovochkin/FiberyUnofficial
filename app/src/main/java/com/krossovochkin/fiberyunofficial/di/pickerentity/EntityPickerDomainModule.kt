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
import com.krossovochkin.fiberyunofficial.entitycreatedomain.EntityCreateInteractor
import com.krossovochkin.fiberyunofficial.entitycreatedomain.EntityCreateInteractorImpl
import com.krossovochkin.fiberyunofficial.entitycreatedomain.EntityCreateRepository
import com.krossovochkin.fiberyunofficial.entitypicker.domain.EntityPickerRepository
import com.krossovochkin.fiberyunofficial.entitypicker.domain.GetEntityListInteractor
import com.krossovochkin.fiberyunofficial.entitypicker.domain.GetEntityListInteractorImpl
import com.krossovochkin.fiberyunofficial.entitypicker.domain.GetEntityTypeSchemaInteractor
import com.krossovochkin.fiberyunofficial.entitypicker.domain.GetEntityTypeSchemaInteractorImpl
import dagger.Module
import dagger.Provides

@Module
object EntityPickerDomainModule {

    @JvmStatic
    @Provides
    fun getEntityListInteractor(
        entityPickerRepository: EntityPickerRepository
    ): GetEntityListInteractor {
        return GetEntityListInteractorImpl(entityPickerRepository)
    }

    @JvmStatic
    @Provides
    fun getEntityTypeInteractor(
        fiberyApiRepository: FiberyApiRepository
    ): GetEntityTypeSchemaInteractor {
        return GetEntityTypeSchemaInteractorImpl(fiberyApiRepository)
    }

    @JvmStatic
    @Provides
    fun createEntityInteractor(
        entityCreateRepository: EntityCreateRepository
    ): EntityCreateInteractor {
        return EntityCreateInteractorImpl(
            entityCreateRepository
        )
    }
}
