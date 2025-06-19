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
package com.krossovochkin.fiberyunofficial.pickermultiselect.presentation

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.krossovochkin.core.presentation.result.parentListener
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.domain.FieldData
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData

class PickerMultiSelectDialogFragment(
    viewModelFactory: PickerMultiSelectViewModel.Factory,
    argsProvider: ArgsProvider
) : DialogFragment() {

    private val viewModel: PickerMultiSelectViewModel by viewModels {
        PickerMultiSelectViewModel.provideFactory(
            viewModelFactory,
            argsProvider.getPickerMultiSelectArgs()
        )
    }

    private val parentListener: ParentListener by parentListener()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val fieldSchema = viewModel.fieldSchema
        val item = viewModel.item
        val selectedItemsIds = item.selectedValues.map { it.id }
        val selectedItemIds = item.values
            .mapIndexed { _, enumItemData -> enumItemData.id in selectedItemsIds }
            .toBooleanArray()
        return AlertDialog.Builder(requireContext())
            .setMultiChoiceItems(
                item.values.map { it.title }.toTypedArray(),
                selectedItemIds
            ) { _, index, isChecked ->
                selectedItemIds[index] = isChecked
            }
            .setTitle(item.title)
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val selectedItems = item.values.filterIndexed { index, _ -> selectedItemIds[index] }
                val addedItems = selectedItems.filter { value -> value !in item.selectedValues }
                val removedItems = item.selectedValues.filter { value -> value !in selectedItems }
                parentListener.onMultiSelectPicked(
                    fieldSchema = fieldSchema,
                    addedItems = addedItems,
                    removedItems = removedItems
                )
            }
            .create()
    }

    data class Args(
        val parentEntityData: ParentEntityData,
        val item: FieldData.MultiSelectFieldData
    )

    fun interface ArgsProvider {

        fun getPickerMultiSelectArgs(): Args
    }

    interface ParentListener {

        fun onMultiSelectPicked(
            fieldSchema: FiberyFieldSchema,
            addedItems: List<FieldData.EnumItemData>,
            removedItems: List<FieldData.EnumItemData>
        )
    }
}
