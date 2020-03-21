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
package by.krossovochkin.fiberyunofficial.entitypicker.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import by.krossovochkin.fiberyunofficial.entitypicker.domain.GetEntityListInteractor
import by.krossovochkin.fiberyunofficial.entitypicker.domain.GetEntityTypeSchemaInteractor
import dagger.Module
import dagger.Provides

@Module
object EntityPickerPresentationModule {

    @JvmStatic
    @Provides
    fun entityPickerArgs(
        fragment: Fragment,
        entityPickerArgsProvider: EntityPickerFragment.ArgsProvider
    ): EntityPickerFragment.Args {
        return entityPickerArgsProvider.getEntityPickerArgs(fragment.requireArguments())
    }

    @JvmStatic
    @Provides
    fun entityPickerViewModel(
        fragment: Fragment,
        entityPickerViewModelFactory: EntityPickerViewModelFactory
    ): EntityPickerViewModel {
        return ViewModelProvider(fragment, entityPickerViewModelFactory).get()
    }

    @JvmStatic
    @Provides
    fun entityPickerViewModelFactory(
        getEntityTypeSchemaInteractor: GetEntityTypeSchemaInteractor,
        getEntityListInteractor: GetEntityListInteractor,
        entityPickerArgs: EntityPickerFragment.Args
    ): EntityPickerViewModelFactory {
        return EntityPickerViewModelFactory(
            getEntityTypeSchemaInteractor,
            getEntityListInteractor,
            entityPickerArgs
        )
    }
}
