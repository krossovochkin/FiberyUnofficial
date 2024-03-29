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
package com.krossovochkin.fiberyunofficial.entitylist.domain

import com.krossovochkin.fiberyunofficial.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityFilterData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntitySortData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData

interface EntityListRepository {

    suspend fun getEntityList(
        entityType: FiberyEntityTypeSchema,
        offset: Int,
        pageSize: Int,
        parentEntityData: ParentEntityData?
    ): List<FiberyEntityData>

    fun setEntityListFilter(entityType: FiberyEntityTypeSchema, filter: FiberyEntityFilterData)

    fun setEntityListSort(entityType: FiberyEntityTypeSchema, sort: FiberyEntitySortData)

    fun getEntityListFilter(entityType: FiberyEntityTypeSchema): FiberyEntityFilterData

    fun getEntityListSort(entityType: FiberyEntityTypeSchema): FiberyEntitySortData

    suspend fun removeRelation(
        parentEntityData: ParentEntityData,
        childEntity: FiberyEntityData
    )

    suspend fun addRelation(
        parentEntityData: ParentEntityData,
        childEntity: FiberyEntityData
    )
}
