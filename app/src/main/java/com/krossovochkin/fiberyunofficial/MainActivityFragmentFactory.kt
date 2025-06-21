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

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.krossovochkin.commentlist.presentation.CommentListFragment
import com.krossovochkin.commentlist.presentation.CommentListFragmentArgs
import com.krossovochkin.fiberyunofficial.applist.presentation.AppListFragment
import com.krossovochkin.fiberyunofficial.di.commentlist.DaggerCommentListComponent
import com.krossovochkin.fiberyunofficial.di.entitycreate.DaggerEntityCreateComponent
import com.krossovochkin.fiberyunofficial.di.entitydetails.DaggerEntityDetailsComponent
import com.krossovochkin.fiberyunofficial.di.entitylist.DaggerEntityListComponent
import com.krossovochkin.fiberyunofficial.di.entitytypelist.DaggerEntityTypeListComponent
import com.krossovochkin.fiberyunofficial.di.filelist.DaggerFileListComponent
import com.krossovochkin.fiberyunofficial.di.login.DaggerLoginComponent
import com.krossovochkin.fiberyunofficial.di.pickerentity.DaggerEntityPickerComponent
import com.krossovochkin.fiberyunofficial.di.pickerfilter.DaggerPickerFilterComponent
import com.krossovochkin.fiberyunofficial.di.pickermultiselect.DaggerPickerMultiSelectComponent
import com.krossovochkin.fiberyunofficial.di.pickersingleselect.DaggerPickerSingleSelectComponent
import com.krossovochkin.fiberyunofficial.di.pickersort.DaggerPickerSortComponent
import com.krossovochkin.fiberyunofficial.entitycreate.presentation.EntityCreateFragment
import com.krossovochkin.fiberyunofficial.entitycreate.presentation.EntityCreateFragmentArgs
import com.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragment
import com.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragmentArgs
import com.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragment
import com.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragmentArgs
import com.krossovochkin.fiberyunofficial.entitypicker.presentation.EntityPickerFragment
import com.krossovochkin.fiberyunofficial.entitypicker.presentation.EntityPickerFragmentArgs
import com.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragment
import com.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragmentArgs
import com.krossovochkin.fiberyunofficial.login.presentation.LoginFragment
import com.krossovochkin.fiberyunofficial.pickerfilter.presentation.PickerFilterFragment
import com.krossovochkin.fiberyunofficial.pickerfilter.presentation.PickerFilterFragmentArgs
import com.krossovochkin.fiberyunofficial.pickermultiselect.presentation.PickerMultiSelectDialogFragment
import com.krossovochkin.fiberyunofficial.pickermultiselect.presentation.PickerMultiSelectDialogFragmentArgs
import com.krossovochkin.fiberyunofficial.pickersingleselect.presentation.PickerSingleSelectDialogFragment
import com.krossovochkin.fiberyunofficial.pickersingleselect.presentation.PickerSingleSelectDialogFragmentArgs
import com.krossovochkin.fiberyunofficial.pickersort.presentation.PickerSortFragment
import com.krossovochkin.fiberyunofficial.pickersort.presentation.PickerSortFragmentArgs
import com.krossovochkin.filelist.presentation.FileListFragment
import com.krossovochkin.filelist.presentation.FileListFragmentArgs

private class ArgsExtractor {

    var fragment: Fragment? = null

    fun extract(): Bundle {
        return fragment!!.requireArguments()
    }
}
