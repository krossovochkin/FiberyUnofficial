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
package by.krossovochkin.fiberyunofficial.entitydetails.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import by.krossovochkin.fiberyunofficial.entitydetails.domain.GetEntityDetailsInteractor
import by.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateEntityFieldInteractor
import by.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateSingleSelectFieldInteractor
import dagger.Module
import dagger.Provides

@Module
object EntityDetailsPresentationModule {

    @JvmStatic
    @Provides
    fun entityDetailsViewModel(
        fragment: Fragment,
        entityDetailsViewModelFactory: EntityDetailsViewModelFactory
    ): EntityDetailsViewModel {
        return ViewModelProvider(fragment, entityDetailsViewModelFactory).get()
    }

    @JvmStatic
    @Provides
    fun entityDetailsViewModelFactory(
        getEntityDetailsInteractor: GetEntityDetailsInteractor,
        updateSingleSelectFieldInteractor: UpdateSingleSelectFieldInteractor,
        updateEntityFieldInteractor: UpdateEntityFieldInteractor,
        entityDetailsArgs: EntityDetailsFragment.Args
    ): EntityDetailsViewModelFactory {
        return EntityDetailsViewModelFactory(
            getEntityDetailsInteractor,
            updateSingleSelectFieldInteractor,
            updateEntityFieldInteractor,
            entityDetailsArgs
        )
    }

    @JvmStatic
    @Provides
    fun entityDetailsArgs(
        fragment: Fragment,
        entityDetailsArgsProvider: EntityDetailsFragment.ArgsProvider
    ): EntityDetailsFragment.Args {
        return entityDetailsArgsProvider.getEntityDetailsArgs(fragment.requireArguments())
    }
}
