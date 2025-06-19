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
package com.krossovochkin.fiberyunofficial.pickersingleselect.presentation

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.krossovochkin.core.presentation.result.parentListener
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.domain.FieldData
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData

class PickerSingleSelectDialogFragment(
    viewModelFactory: PickerSingleSelectViewModel.Factory,
    argsProvider: ArgsProvider
) : DialogFragment() {

    private val viewModel: PickerSingleSelectViewModel by viewModels {
        PickerSingleSelectViewModel.provideFactory(
            viewModelFactory,
            argsProvider.getPickerSingleSelectArgs()
        )
    }

    private val parentListener: ParentListener by parentListener()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val fieldSchema = viewModel.fieldSchema
        val item = viewModel.item
        var selectedIndex: Int = item.values.indexOf(item.selectedValue)
        return AlertDialog.Builder(requireContext())
            .setSingleChoiceItems(
                item.values.map { it.title }.toTypedArray(),
                selectedIndex
            ) { _, index -> selectedIndex = index }
            .setTitle(item.title)
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                parentListener.onSingleSelectPicked(fieldSchema, item.values[selectedIndex])
            }
            .create()
    }

    data class Args(
        val parentEntityData: ParentEntityData,
        val item: FieldData.SingleSelectFieldData
    )

    fun interface ArgsProvider {

        fun getPickerSingleSelectArgs(): Args
    }

    interface ParentListener {

        fun onSingleSelectPicked(fieldSchema: FiberyFieldSchema, item: FieldData.EnumItemData)

        fun onBackPressed()
    }
}
