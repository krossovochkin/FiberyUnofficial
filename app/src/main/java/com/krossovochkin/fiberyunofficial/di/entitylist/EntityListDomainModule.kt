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
package com.krossovochkin.fiberyunofficial.di.entitylist

import com.krossovochkin.fiberyunofficial.entitylist.domain.AddEntityRelationInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.AddEntityRelationInteractorImpl
import com.krossovochkin.fiberyunofficial.entitylist.domain.EntityListRepository
import com.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListFilterInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListFilterInteractorImpl
import com.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListInteractorImpl
import com.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListSortInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListSortInteractorImpl
import com.krossovochkin.fiberyunofficial.entitylist.domain.RemoveEntityRelationInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.RemoveEntityRelationInteractorImpl
import com.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListFilterInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListFilterInteractorImpl
import com.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListSortInteractor
import com.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListSortInteractorImpl
import dagger.Module
import dagger.Provides

@Module
object EntityListDomainModule {

    @JvmStatic
    @Provides
    fun getEntityListInteractor(
        entityListRepository: EntityListRepository
    ): GetEntityListInteractor {
        return GetEntityListInteractorImpl(entityListRepository)
    }

    @JvmStatic
    @Provides
    fun setEntityListFilterInteractor(
        entityListRepository: EntityListRepository
    ): SetEntityListFilterInteractor {
        return SetEntityListFilterInteractorImpl(entityListRepository)
    }

    @JvmStatic
    @Provides
    fun setEntityListSortInteractor(
        entityListRepository: EntityListRepository
    ): SetEntityListSortInteractor {
        return SetEntityListSortInteractorImpl(entityListRepository)
    }

    @JvmStatic
    @Provides
    fun getEntityListFilterInteractor(
        entityListRepository: EntityListRepository
    ): GetEntityListFilterInteractor {
        return GetEntityListFilterInteractorImpl(entityListRepository)
    }

    @JvmStatic
    @Provides
    fun getEntityListSortInteractor(
        entityListRepository: EntityListRepository
    ): GetEntityListSortInteractor {
        return GetEntityListSortInteractorImpl(entityListRepository)
    }

    @JvmStatic
    @Provides
    fun removeEntityRelationInteractor(
        entityListRepository: EntityListRepository
    ): RemoveEntityRelationInteractor {
        return RemoveEntityRelationInteractorImpl(entityListRepository)
    }

    @JvmStatic
    @Provides
    fun addEntityRelationInteractor(
        entityListRepository: EntityListRepository
    ): AddEntityRelationInteractor {
        return AddEntityRelationInteractorImpl(entityListRepository)
    }
}
