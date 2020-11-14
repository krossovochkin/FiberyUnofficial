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
import by.krossovochkin.fiberyunofficial.entitydetails.domain.GetEntityDetailsInteractor
import by.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateEntityFieldInteractor
import by.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateMultiSelectFieldInteractor
import by.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateSingleSelectFieldInteractor
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragment
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsViewModelFactory
import dagger.Module
import dagger.Provides

@Module
object EntityDetailsPresentationModule {

    @JvmStatic
    @Provides
    fun entityDetailsViewModelFactoryProducer(
        getEntityDetailsInteractor: GetEntityDetailsInteractor,
        updateSingleSelectFieldInteractor: UpdateSingleSelectFieldInteractor,
        updateMultiSelectFieldInteractor: UpdateMultiSelectFieldInteractor,
        updateEntityFieldInteractor: UpdateEntityFieldInteractor,
        deleteEntityInteractor: DeleteEntityInteractor,
        argsProvider: EntityDetailsFragment.ArgsProvider
    ): () -> EntityDetailsViewModelFactory {
        return {
            EntityDetailsViewModelFactory(
                getEntityDetailsInteractor,
                updateSingleSelectFieldInteractor,
                updateMultiSelectFieldInteractor,
                updateEntityFieldInteractor,
                deleteEntityInteractor,
                argsProvider.getEntityDetailsArgs()
            )
        }
    }
}
