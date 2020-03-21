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
package by.krossovochkin.fiberyunofficial.applist.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import by.krossovochkin.fiberyunofficial.applist.domain.GetAppListInteractor
import dagger.Module
import dagger.Provides

@Module
object AppListPresentationModule {

    @JvmStatic
    @Provides
    fun appListViewModel(
        fragment: Fragment,
        appListViewModelFactory: AppListViewModelFactory
    ): AppListViewModel {
        return ViewModelProvider(fragment, appListViewModelFactory).get()
    }

    @JvmStatic
    @Provides
    fun appListViewModelFactory(
        getAppListInteractor: GetAppListInteractor
    ): AppListViewModelFactory {
        return AppListViewModelFactory(
            getAppListInteractor
        )
    }
}
