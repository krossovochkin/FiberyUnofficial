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

import com.krossovochkin.fiberyunofficial.api.FiberyApiConstants
import com.krossovochkin.fiberyunofficial.api.FiberyApiRepository
import com.krossovochkin.fiberyunofficial.domain.FiberyAppData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import javax.inject.Inject

class GetEntityTypeListInteractor @Inject constructor(
    private val fiberyApiRepository: FiberyApiRepository,
) {

    suspend fun execute(appData: FiberyAppData): List<FiberyEntityTypeSchema> {
        val typesDto = fiberyApiRepository.getTypeSchemas()
            .filter { it.meta.isDomain && it.name != FiberyApiConstants.Type.USER.value }
        return typesDto
            .filter { typeDto ->
                typeDto.name.startsWith(appData.name)
            }
    }
}
