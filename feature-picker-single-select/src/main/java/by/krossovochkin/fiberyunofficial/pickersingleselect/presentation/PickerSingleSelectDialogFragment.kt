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
package by.krossovochkin.fiberyunofficial.pickersingleselect.presentation

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.domain.FieldData
import by.krossovochkin.fiberyunofficial.core.domain.ParentEntityData
import by.krossovochkin.fiberyunofficial.pickersingleselect.DaggerPickerSingleSelectComponent
import by.krossovochkin.fiberyunofficial.pickersingleselect.PickerSingleSelectParentComponent
import javax.inject.Inject

class PickerSingleSelectDialogFragment(
    private val pickerSingleSelectParentComponent: PickerSingleSelectParentComponent
) : DialogFragment() {

    @Inject
    lateinit var viewModel: PickerSingleSelectViewModel

    private var parentListener: ParentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerPickerSingleSelectComponent.builder()
            .fragment(this)
            .pickerSingleSelectParentComponent(pickerSingleSelectParentComponent)
            .build()
            .inject(this)
    }

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
                parentListener?.onSingleSelectPicked(fieldSchema, item.values[selectedIndex])
            }
            .create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentListener = context as ParentListener
    }

    override fun onDetach() {
        super.onDetach()
        parentListener = null
    }

    data class Args(
        val parentEntityData: ParentEntityData,
        val item: FieldData.SingleSelectFieldData
    )

    interface ArgsProvider {

        fun getPickerSingleSelectArgs(arguments: Bundle): Args
    }

    interface ParentListener {

        fun onSingleSelectPicked(fieldSchema: FiberyFieldSchema, item: FieldData.EnumItemData)

        fun onBackPressed()
    }
}
