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
package com.krossovochkin.fiberyunofficial.entitydetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krossovochkin.fiberyunofficial.entitydetails.domain.DeleteEntityInteractor
import com.krossovochkin.fiberyunofficial.entitydetails.domain.GetEntityDetailsInteractor
import com.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateEntityFieldInteractor
import com.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateMultiSelectFieldInteractor
import com.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateSingleSelectFieldInteractor

class EntityDetailsViewModelFactory(
    private val getEntityDetailsInteractor: GetEntityDetailsInteractor,
    private val updateSingleSelectFieldInteractor: UpdateSingleSelectFieldInteractor,
    private val updateMultiSelectFieldInteractor: UpdateMultiSelectFieldInteractor,
    private val updateEntityFieldInteractor: UpdateEntityFieldInteractor,
    private val deleteEntityInteractor: DeleteEntityInteractor,
    private val entityDetailsArgs: EntityDetailsFragment.Args
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass == EntityDetailsViewModel::class.java) {
            EntityDetailsViewModelImpl(
                getEntityDetailsInteractor,
                updateSingleSelectFieldInteractor,
                updateMultiSelectFieldInteractor,
                updateEntityFieldInteractor,
                deleteEntityInteractor,
                entityDetailsArgs
            ) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
