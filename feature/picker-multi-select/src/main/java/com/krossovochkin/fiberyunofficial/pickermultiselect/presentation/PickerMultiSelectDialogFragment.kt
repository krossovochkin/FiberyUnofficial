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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.krossovochkin.core.presentation.result.parentListener
import com.krossovochkin.fiberyunofficial.domain.FiberyFieldSchema
import com.krossovochkin.fiberyunofficial.domain.FieldData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PickerMultiSelectDialogFragment : DialogFragment() {

    private val viewModel: PickerMultiSelectViewModel by viewModels()

    private val parentListener: ParentListener by parentListener()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            PickerMultiSelectScreen(
                item = viewModel.item,
                onConfirm = { addedItems, removedItems ->
                    parentListener.onMultiSelectPicked(
                        fieldSchema = viewModel.fieldSchema,
                        addedItems = addedItems,
                        removedItems = removedItems
                    )
                    dismiss()
                },
                onDismiss = {
                    dismiss()
                }
            )
        }
    }

    interface ParentListener {

        fun onMultiSelectPicked(
            fieldSchema: FiberyFieldSchema,
            addedItems: List<FieldData.EnumItemData>,
            removedItems: List<FieldData.EnumItemData>
        )
    }
}
