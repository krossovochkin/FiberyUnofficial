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

package by.krossovochkin.fiberyunofficial

import by.krossovochkin.fiberyunofficial.applist.AppListParentComponent
import by.krossovochkin.fiberyunofficial.entitycreate.EntityCreateParentComponent
import by.krossovochkin.fiberyunofficial.entitycreate.presentation.EntityCreateFragment
import by.krossovochkin.fiberyunofficial.entitydetails.EntityDetailsParentComponent
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragment
import by.krossovochkin.fiberyunofficial.entitylist.EntityListParentComponent
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragment
import by.krossovochkin.fiberyunofficial.entitypicker.EntityPickerParentComponent
import by.krossovochkin.fiberyunofficial.entitypicker.presentation.EntityPickerFragment
import by.krossovochkin.fiberyunofficial.entitytypelist.EntityTypeListParentComponent
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragment
import by.krossovochkin.fiberyunofficial.login.LoginParentComponent
import by.krossovochkin.fiberyunofficial.pickermultiselect.PickerMultiSelectParentComponent
import by.krossovochkin.fiberyunofficial.pickermultiselect.presentation.PickerMultiSelectDialogFragment
import by.krossovochkin.fiberyunofficial.pickersingleselect.PickerSingleSelectParentComponent
import by.krossovochkin.fiberyunofficial.pickersingleselect.presentation.PickerSingleSelectDialogFragment
import com.krossovochkin.fiberyunofficial.pickerfilter.PickerFilterParentComponent
import com.krossovochkin.fiberyunofficial.pickerfilter.presentation.PickerFilterFragment
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Scope

@MainActivityScope
@Component(
    modules = [MainActivityModule::class],
    dependencies = [ApplicationComponent::class]
)
interface MainActivityComponent :
    LoginParentComponent,
    AppListParentComponent,
    EntityTypeListParentComponent,
    EntityListParentComponent,
    EntityDetailsParentComponent,
    EntityCreateParentComponent,
    EntityPickerParentComponent,
    PickerSingleSelectParentComponent,
    PickerMultiSelectParentComponent,
    PickerFilterParentComponent {

    @Component.Factory
    interface Factory {

        fun create(
            applicationComponent: ApplicationComponent,
            @BindsInstance mainActivityArgsProvider: MainActivityArgsProvider
        ): MainActivityComponent
    }
}

@Module
abstract class MainActivityModule {

    @MainActivityScope
    @Binds
    abstract fun entityTypeListArgsProvider(
        mainActivityArgsProvider: MainActivityArgsProvider
    ): EntityTypeListFragment.ArgsProvider

    @MainActivityScope
    @Binds
    abstract fun entityListArgsProvider(
        mainActivityArgsProvider: MainActivityArgsProvider
    ): EntityListFragment.ArgsProvider

    @MainActivityScope
    @Binds
    abstract fun entityDetailsArgsProvider(
        mainActivityArgsProvider: MainActivityArgsProvider
    ): EntityDetailsFragment.ArgsProvider

    @MainActivityScope
    @Binds
    abstract fun entityCreateArgsProvider(
        mainActivityArgsProvider: MainActivityArgsProvider
    ): EntityCreateFragment.ArgsProvider

    @MainActivityScope
    @Binds
    abstract fun entityPickerArgsProvider(
        mainActivityArgsProvider: MainActivityArgsProvider
    ): EntityPickerFragment.ArgsProvider

    @MainActivityScope
    @Binds
    abstract fun pickerSingleSelectArgsProvider(
        mainActivityArgsProvider: MainActivityArgsProvider
    ): PickerSingleSelectDialogFragment.ArgsProvider

    @MainActivityScope
    @Binds
    abstract fun pickerMultiSelectArgsProvider(
        mainActivityArgsProvider: MainActivityArgsProvider
    ): PickerMultiSelectDialogFragment.ArgsProvider

    @MainActivityScope
    @Binds
    abstract fun pickerFilterArgsProvider(
        mainActivityArgsProvider: MainActivityArgsProvider
    ): PickerFilterFragment.ArgsProvider
}

@Scope
@Retention
annotation class MainActivityScope
