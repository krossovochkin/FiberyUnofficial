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

import by.krossovochkin.fiberyunofficial.applist.presentation.AppListFragment
import by.krossovochkin.fiberyunofficial.entitycreate.presentation.EntityCreateFragment
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragment
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragment
import by.krossovochkin.fiberyunofficial.entitypicker.presentation.EntityPickerFragment
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragment
import by.krossovochkin.fiberyunofficial.login.presentation.LoginFragment
import by.krossovochkin.fiberyunofficial.pickermultiselect.presentation.PickerMultiSelectDialogFragment
import by.krossovochkin.fiberyunofficial.pickersingleselect.presentation.PickerSingleSelectDialogFragment
import com.krossovochkin.fiberyunofficial.pickerfilter.presentation.PickerFilterFragment

interface MainActivityListener :
    LoginFragment.ParentListener,
    AppListFragment.ParentListener,
    EntityTypeListFragment.ParentListener,
    EntityListFragment.ParentListener,
    EntityDetailsFragment.ParentListener,
    EntityCreateFragment.ParentListener,
    EntityPickerFragment.ParentListener,
    PickerSingleSelectDialogFragment.ParentListener,
    PickerMultiSelectDialogFragment.ParentListener,
    PickerFilterFragment.ParentListener
