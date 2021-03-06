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

package by.krossovochkin.fiberyunofficial.di.pickerfilter

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyApiRepository
import by.krossovochkin.fiberyunofficial.core.data.serialization.Serializer
import by.krossovochkin.fiberyunofficial.core.presentation.ResProvider
import com.krossovochkin.fiberyunofficial.pickerfilter.presentation.PickerFilterFragment
import com.krossovochkin.fiberyunofficial.pickerfilter.presentation.PickerFilterViewModelFactory
import dagger.Module
import dagger.Provides

@Module
object PickerFilterPresentationModule {

    @JvmStatic
    @Provides
    fun pickerFilterViewModelFactoryProducer(
        fiberyApiRepository: FiberyApiRepository,
        resProvider: ResProvider,
        argsProvider: PickerFilterFragment.ArgsProvider,
        serializer: Serializer
    ): () -> PickerFilterViewModelFactory {
        return {
            PickerFilterViewModelFactory(
                pickerFilterArgs = argsProvider.getPickerFilterArgs(),
                fiberyApiRepository = fiberyApiRepository,
                resProvider = resProvider,
                serializer = serializer
            )
        }
    }
}
