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
package by.krossovochkin.fiberyunofficial.applist

import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.applist.data.AppListDataModule
import by.krossovochkin.fiberyunofficial.applist.domain.AppListDomainModule
import by.krossovochkin.fiberyunofficial.applist.presentation.AppListFragment
import by.krossovochkin.fiberyunofficial.applist.presentation.AppListPresentationModule
import by.krossovochkin.fiberyunofficial.applist.presentation.AppListViewModel
import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

interface AppListParentComponent : GlobalDependencies

@AppList
@Component(
    modules = [
        AppListDataModule::class,
        AppListDomainModule::class,
        AppListPresentationModule::class
    ],
    dependencies = [AppListParentComponent::class]
)
interface AppListComponent {

    fun appListViewModel(): AppListViewModel

    fun inject(fragment: AppListFragment)

    @Component.Factory
    interface Factory {

        fun create(
            appListParentComponent: AppListParentComponent,
            @BindsInstance fragment: Fragment
        ): AppListComponent
    }
}

@Scope
@Retention
annotation class AppList
