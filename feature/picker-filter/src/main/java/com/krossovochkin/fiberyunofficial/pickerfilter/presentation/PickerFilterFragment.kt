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

package com.krossovochkin.fiberyunofficial.pickerfilter.presentation

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.krossovochkin.core.presentation.animation.setupTransformEnterTransition
import com.krossovochkin.core.presentation.list.ListItem
import com.krossovochkin.core.presentation.list.initRecyclerView
import com.krossovochkin.core.presentation.navigation.initNavigation
import com.krossovochkin.core.presentation.result.parentListener
import com.krossovochkin.core.presentation.system.updateInsetMargins
import com.krossovochkin.core.presentation.ui.toolbar.initToolbar
import com.krossovochkin.core.presentation.viewbinding.viewBinding
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityFilterData
import com.krossovochkin.fiberyunofficial.pickerfilter.R
import com.krossovochkin.fiberyunofficial.pickerfilter.databinding.PickerFilterFragmentBinding
import com.krossovochkin.fiberyunofficial.pickerfilter.databinding.PickerFilterItemAddBinding
import com.krossovochkin.fiberyunofficial.pickerfilter.databinding.PickerFilterItemEmptyBinding
import com.krossovochkin.fiberyunofficial.pickerfilter.databinding.PickerFilterItemMergeTypeBinding
import com.krossovochkin.fiberyunofficial.pickerfilter.databinding.PickerFilterItemSingleSelectBinding
import com.krossovochkin.fiberyunofficial.pickerfilter.domain.FilterMergeType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

@AndroidEntryPoint
class PickerFilterFragment : Fragment(R.layout.picker_filter_fragment) {

    private val viewModel: PickerFilterViewModel by viewModels()

    private val binding by viewBinding(PickerFilterFragmentBinding::bind)

    private val parentListener: ParentListener by parentListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTransformEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initNavigation(
            navigationData = viewModel.navigation,
            transitionName = requireContext()
                .getString(R.string.picker_filter_root_transition_name)
        ) { event ->
            when (event) {
                is PickerFilterNavEvent.ApplyFilterEvent -> {
                    parentListener.onFilterSelected(filter = event.filter)
                }
                PickerFilterNavEvent.BackEvent -> parentListener.onBackPressed()
            }
        }

        initToolbar(
            toolbar = binding.pickerFilterToolbar,
            toolbarData = MutableStateFlow(viewModel.toolbarViewState),
            onBackPressed = { viewModel.onBackPressed() }
        )

        initRecyclerView(
            recyclerView = binding.recyclerView,
            itemsFlow = viewModel.items,
            adapterDelegateViewBinding<FilterMergeTypeItem, ListItem, PickerFilterItemMergeTypeBinding>(
                viewBinding = { inflater, parent ->
                    PickerFilterItemMergeTypeBinding.inflate(inflater, parent, false)
                }
            ) {
                bind {
                    binding.mergeTypeSpinner.setup(
                        items = FilterMergeType.values().map { getString(it.displayNameResId) },
                        selectedItem = getString(item.type.displayNameResId)
                    ) { viewModel.onMergeTypeSelected(item.type) }
                }
                onViewRecycled { binding.mergeTypeSpinner.recycle() }
            },
            adapterDelegateViewBinding<AddFilterItem, ListItem, PickerFilterItemAddBinding>(
                viewBinding = { inflater, parent ->
                    PickerFilterItemAddBinding.inflate(inflater, parent, false)
                }
            ) {
                bind {
                    binding.addButton.setOnClickListener {
                        viewModel.onAddFilterClicked()
                    }
                }
            },
            adapterDelegateViewBinding<EmptyFilterItem, ListItem, PickerFilterItemEmptyBinding>(
                viewBinding = { inflater, parent ->
                    PickerFilterItemEmptyBinding.inflate(inflater, parent, false)
                }
            ) {
                bind {
                    binding.spinner.setup(
                        items = item.fields.map { it.displayName }
                    ) { position ->
                        viewModel.onFieldSelected(
                            absoluteAdapterPosition - 1,
                            item.fields.getOrNull(position)
                        )
                    }
                }
                onViewRecycled { binding.spinner.recycle() }
            },
            adapterDelegateViewBinding<SingleSelectFilterItem, ListItem, PickerFilterItemSingleSelectBinding>(
                viewBinding = { inflater, parent ->
                    PickerFilterItemSingleSelectBinding.inflate(inflater, parent, false)
                }
            ) {
                bind {
                    binding.fieldTypeSpinner.setup(
                        items = item.fields.map { it.displayName },
                        selectedItem = item.field.displayName
                    ) { position ->
                        viewModel.onFieldSelected(
                            absoluteAdapterPosition - 1,
                            item.fields.getOrNull(position)
                        )
                    }

                    binding.conditionSpinner.setup(
                        items = item.conditions.map { getString(it.displayStringResId) },
                        selectedItem = item.condition?.let { getString(it.displayStringResId) }
                    ) { position ->
                        viewModel.onConditionSelected(
                            absoluteAdapterPosition - 1,
                            item.conditions.getOrNull(position)
                        )
                    }

                    binding.singleSelectValueSpinner.setup(
                        items = item.values.map { it.title },
                        selectedItem = item.selectedValue?.title
                    ) { position ->
                        viewModel.onSingleSelectValueSelected(
                            absoluteAdapterPosition - 1,
                            item.values.getOrNull(position)
                        )
                    }
                }

                onViewRecycled {
                    binding.fieldTypeSpinner.recycle()
                    binding.conditionSpinner.recycle()
                    binding.singleSelectValueSpinner.recycle()
                }
            }
        )

        binding.applyAction.setOnClickListener { viewModel.applyFilter() }
        binding.applyAction.updateInsetMargins(bottom = true)
    }

    private inline fun Spinner.setup(
        items: List<String>,
        selectedItem: String? = null,
        crossinline onSelection: (Int) -> Unit
    ) {
        this.adapter = ArrayAdapter(
            context,
            android.R.layout.simple_list_item_1,
            listOf("") + items
        )

        items.indexOf(selectedItem).let {
            this.setSelection(
                if (it == -1) 0 else it + 1,
                false
            )
        }
        this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            var isInitial = true

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (isInitial) {
                    isInitial = false
                    return
                }
                onSelection(position - 1)
            }
        }
    }

    private fun Spinner.recycle() {
        this.onItemSelectedListener = null
        this.adapter = null
    }

    interface ParentListener {

        fun onFilterSelected(filter: FiberyEntityFilterData)

        fun onBackPressed()
    }
}
