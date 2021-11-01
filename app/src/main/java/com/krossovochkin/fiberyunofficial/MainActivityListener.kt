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

package com.krossovochkin.fiberyunofficial

import com.krossovochkin.commentlist.presentation.CommentListFragment
import com.krossovochkin.fiberyunofficial.applist.presentation.AppListFragment
import com.krossovochkin.fiberyunofficial.entitycreate.presentation.EntityCreateFragment
import com.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragment
import com.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragment
import com.krossovochkin.fiberyunofficial.entitypicker.presentation.EntityPickerFragment
import com.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragment
import com.krossovochkin.fiberyunofficial.login.presentation.LoginFragment
import com.krossovochkin.fiberyunofficial.pickerfilter.presentation.PickerFilterFragment
import com.krossovochkin.fiberyunofficial.pickermultiselect.presentation.PickerMultiSelectDialogFragment
import com.krossovochkin.fiberyunofficial.pickersingleselect.presentation.PickerSingleSelectDialogFragment
import com.krossovochkin.fiberyunofficial.pickersort.presentation.PickerSortFragment
import com.krossovochkin.filelist.presentation.FileListFragment

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
    PickerFilterFragment.ParentListener,
    PickerSortFragment.ParentListener,
    FileListFragment.ParentListener,
    CommentListFragment.ParentListener
