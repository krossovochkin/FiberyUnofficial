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
package by.krossovochkin.fiberyunofficial.di.entitydetails

import by.krossovochkin.fiberyunofficial.entitydetails.domain.DeleteEntityInteractor
import by.krossovochkin.fiberyunofficial.entitydetails.domain.DeleteEntityInteractorImpl
import by.krossovochkin.fiberyunofficial.entitydetails.domain.EntityDetailsRepository
import by.krossovochkin.fiberyunofficial.entitydetails.domain.GetEntityDetailsInteractor
import by.krossovochkin.fiberyunofficial.entitydetails.domain.GetEntityDetailsInteractorImpl
import by.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateEntityFieldInteractor
import by.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateEntityFieldInteractorImpl
import by.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateMultiSelectFieldInteractor
import by.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateMultiSelectFieldInteractorImpl
import by.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateSingleSelectFieldInteractor
import by.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateSingleSelectFieldInteractorImpl
import dagger.Module
import dagger.Provides

@Module
object EntityDetailsDomainModule {

    @JvmStatic
    @Provides
    fun getEntityDetailsInteractor(
        entityDetailsRepository: EntityDetailsRepository
    ): GetEntityDetailsInteractor {
        return GetEntityDetailsInteractorImpl(entityDetailsRepository)
    }

    @JvmStatic
    @Provides
    fun updateSingleSelectFieldInteractor(
        entityDetailsRepository: EntityDetailsRepository
    ): UpdateSingleSelectFieldInteractor {
        return UpdateSingleSelectFieldInteractorImpl(entityDetailsRepository)
    }

    @JvmStatic
    @Provides
    fun updateMultiSelectFieldInteractor(
        entityDetailsRepository: EntityDetailsRepository
    ): UpdateMultiSelectFieldInteractor {
        return UpdateMultiSelectFieldInteractorImpl(entityDetailsRepository)
    }

    @JvmStatic
    @Provides
    fun updateEntityFieldInteractor(
        entityDetailsRepository: EntityDetailsRepository
    ): UpdateEntityFieldInteractor {
        return UpdateEntityFieldInteractorImpl(entityDetailsRepository)
    }

    @JvmStatic
    @Provides
    fun deleteEntityInteractor(
        entityDetailsRepository: EntityDetailsRepository
    ): DeleteEntityInteractor {
        return DeleteEntityInteractorImpl(entityDetailsRepository)
    }
}
