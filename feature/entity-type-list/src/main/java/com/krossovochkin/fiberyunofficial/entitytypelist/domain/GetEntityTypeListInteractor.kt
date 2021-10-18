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
package com.krossovochkin.fiberyunofficial.entitytypelist.domain

import com.krossovochkin.fiberyunofficial.domain.FiberyAppData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema

interface GetEntityTypeListInteractor {

    suspend fun execute(appData: FiberyAppData): List<FiberyEntityTypeSchema>
}

class GetEntityTypeListInteractorImpl(
    private val entityTypeListRepository: EntityTypeListRepository
) : GetEntityTypeListInteractor {

    override suspend fun execute(appData: FiberyAppData): List<FiberyEntityTypeSchema> {
        return entityTypeListRepository.getEntityTypeList(appData)
    }
}