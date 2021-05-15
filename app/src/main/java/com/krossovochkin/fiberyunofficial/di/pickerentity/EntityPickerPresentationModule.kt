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
package com.krossovochkin.fiberyunofficial.di.pickerentity

import com.krossovochkin.fiberyunofficial.core.domain.entitycreate.EntityCreateInteractor
import com.krossovochkin.fiberyunofficial.entitypicker.domain.GetEntityListInteractor
import com.krossovochkin.fiberyunofficial.entitypicker.domain.GetEntityTypeSchemaInteractor
import com.krossovochkin.fiberyunofficial.entitypicker.presentation.EntityPickerFragment
import com.krossovochkin.fiberyunofficial.entitypicker.presentation.EntityPickerViewModelFactory
import dagger.Module
import dagger.Provides

@Module
object EntityPickerPresentationModule {

    @JvmStatic
    @Provides
    fun entityPickerViewModelFactoryProvider(
        getEntityTypeSchemaInteractor: GetEntityTypeSchemaInteractor,
        getEntityListInteractor: GetEntityListInteractor,
        createEntityInteractor: EntityCreateInteractor,
        argsProvider: EntityPickerFragment.ArgsProvider
    ): () -> EntityPickerViewModelFactory {
        return {
            EntityPickerViewModelFactory(
                getEntityTypeSchemaInteractor,
                getEntityListInteractor,
                createEntityInteractor,
                argsProvider.getEntityPickerArgs()
            )
        }
    }
}
