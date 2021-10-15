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
package com.krossovochkin.fiberyunofficial.entitypicker.domain

import com.krossovochkin.fiberyunofficial.api.FiberyApiRepository
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema

interface GetEntityTypeSchemaInteractor {

    suspend fun execute(fieldSchema: FiberyFieldSchema): FiberyEntityTypeSchema
}

class GetEntityTypeSchemaInteractorImpl(
    private val fiberyApiRepository: FiberyApiRepository
) : GetEntityTypeSchemaInteractor {

    override suspend fun execute(fieldSchema: FiberyFieldSchema): FiberyEntityTypeSchema {
        return fiberyApiRepository.getTypeSchema(fieldSchema.type)
    }
}
