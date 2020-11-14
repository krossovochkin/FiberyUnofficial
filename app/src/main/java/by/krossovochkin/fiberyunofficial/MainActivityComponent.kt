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

import by.krossovochkin.fiberyunofficial.di.applist.AppListParentComponent
import by.krossovochkin.fiberyunofficial.di.entitycreate.EntityCreateParentComponent
import by.krossovochkin.fiberyunofficial.di.entitydetails.EntityDetailsParentComponent
import by.krossovochkin.fiberyunofficial.di.entitylist.EntityListParentComponent
import by.krossovochkin.fiberyunofficial.di.entitytypelist.EntityTypeListParentComponent
import by.krossovochkin.fiberyunofficial.di.login.LoginParentComponent
import by.krossovochkin.fiberyunofficial.di.pickerentity.EntityPickerParentComponent
import by.krossovochkin.fiberyunofficial.di.pickerfilter.PickerFilterParentComponent
import by.krossovochkin.fiberyunofficial.di.pickermultiselect.PickerMultiSelectParentComponent
import by.krossovochkin.fiberyunofficial.di.pickersingleselect.PickerSingleSelectParentComponent
import dagger.Component
import javax.inject.Scope

@MainActivityScope
@Component(
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
            applicationComponent: ApplicationComponent
        ): MainActivityComponent
    }
}

@Scope
@Retention
annotation class MainActivityScope
