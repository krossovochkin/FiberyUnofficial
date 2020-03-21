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
package by.krossovochkin.fiberyunofficial.entitytypelist.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import by.krossovochkin.fiberyunofficial.entitytypelist.domain.GetEntityTypeListInteractor
import dagger.Module
import dagger.Provides

@Module
object EntityTypeListPresentationModule {

    @JvmStatic
    @Provides
    fun entityTypeListArgs(
        fragment: Fragment,
        entityTypeListArgsProvider: EntityTypeListFragment.ArgsProvider
    ): EntityTypeListFragment.Args {
        return entityTypeListArgsProvider.getEntityTypeListArgs(fragment.requireArguments())
    }

    @JvmStatic
    @Provides
    fun entityTypeListViewModel(
        fragment: Fragment,
        entityTypeListViewModelFactory: EntityTypeListViewModelFactory
    ): EntityTypeListViewModel {
        return ViewModelProvider(fragment, entityTypeListViewModelFactory).get()
    }

    @JvmStatic
    @Provides
    fun entityTypeListViewModelFactory(
        getEntityTypeListInteractor: GetEntityTypeListInteractor,
        args: EntityTypeListFragment.Args
    ): EntityTypeListViewModelFactory {
        return EntityTypeListViewModelFactory(
            getEntityTypeListInteractor,
            args
        )
    }
}
