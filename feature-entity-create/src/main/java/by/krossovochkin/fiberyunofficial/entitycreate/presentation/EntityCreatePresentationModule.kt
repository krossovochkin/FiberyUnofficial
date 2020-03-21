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
package by.krossovochkin.fiberyunofficial.entitycreate.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import by.krossovochkin.fiberyunofficial.entitycreate.domain.EntityCreateInteractor
import dagger.Module
import dagger.Provides

@Module
object EntityCreatePresentationModule {

    @JvmStatic
    @Provides
    fun entityCreateArgs(
        fragment: Fragment,
        entityCreateArgsProvider: EntityCreateFragment.ArgsProvider
    ): EntityCreateFragment.Args {
        return entityCreateArgsProvider.getEntityCreateArgs(fragment.requireArguments())
    }

    @JvmStatic
    @Provides
    fun entityCreateViewModel(
        fragment: Fragment,
        entityCreateViewModelFactory: EntityCreateViewModelFactory
    ): EntityCreateViewModel {
        return ViewModelProvider(fragment, entityCreateViewModelFactory).get()
    }

    @JvmStatic
    @Provides
    fun entityCreateViewModelFactory(
        entityCreateArgs: EntityCreateFragment.Args,
        entityCreateInteractor: EntityCreateInteractor
    ): EntityCreateViewModelFactory {
        return EntityCreateViewModelFactory(
            entityCreateArgs,
            entityCreateInteractor
        )
    }
}
