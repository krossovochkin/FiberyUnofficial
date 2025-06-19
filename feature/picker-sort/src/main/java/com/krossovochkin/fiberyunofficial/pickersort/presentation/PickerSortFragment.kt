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

package com.krossovochkin.fiberyunofficial.pickersort.presentation

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
import com.krossovochkin.fiberyunofficial.domain.FiberyEntitySortData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.pickersort.R
import com.krossovochkin.fiberyunofficial.pickersort.databinding.PickerSortFragmentBinding
import com.krossovochkin.fiberyunofficial.pickersort.databinding.PickerSortItemAddBinding
import com.krossovochkin.fiberyunofficial.pickersort.databinding.PickerSortItemBinding
import com.krossovochkin.fiberyunofficial.pickersort.databinding.PickerSortItemEmptyBinding
import kotlinx.coroutines.flow.MutableStateFlow

class PickerSortFragment(
    viewModelFactory: PickerSortViewModel.Factory,
    argsProvider: ArgsProvider
) : Fragment(R.layout.picker_sort_fragment) {

    private val viewModel: PickerSortViewModel by viewModels {
        PickerSortViewModel.provideFactory(
            viewModelFactory,
            argsProvider.getPickerSortArgs()
        )
    }

    private val binding by viewBinding(PickerSortFragmentBinding::bind)

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
                .getString(R.string.picker_sort_root_transition_name)
        ) { event ->
            when (event) {
                is PickerSortNavEvent.ApplySortEvent -> {
                    parentListener.onSortSelected(sort = event.sort)
                }
                PickerSortNavEvent.BackEvent -> parentListener.onBackPressed()
            }
        }

        initToolbar(
            toolbar = binding.pickerSortToolbar,
            toolbarData = MutableStateFlow(viewModel.toolbarViewState),
            onBackPressed = { viewModel.onBackPressed() }
        )

        initRecyclerView(
            recyclerView = binding.recyclerView,
            itemsFlow = viewModel.items,
            adapterDelegateViewBinding<AddSortItem, ListItem, PickerSortItemAddBinding>(
                viewBinding = { inflater, parent ->
                    PickerSortItemAddBinding.inflate(inflater, parent, false)
                }
            ) {
                bind {
                    binding.addButton.setOnClickListener {
                        viewModel.onAddSortClicked()
                    }
                }
            },
            adapterDelegateViewBinding<EmptySortItem, ListItem, PickerSortItemEmptyBinding>(
                viewBinding = { inflater, parent ->
                    PickerSortItemEmptyBinding.inflate(inflater, parent, false)
                }
            ) {
                bind {
                    binding.spinner.setup(
                        items = item.fields.map { it.displayName }
                    ) { position ->
                        viewModel.onFieldSelected(
                            absoluteAdapterPosition,
                            item.fields.getOrNull(position)
                        )
                    }
                }
                onViewRecycled { binding.spinner.recycle() }
            },
            adapterDelegateViewBinding<SelectedSortItem, ListItem, PickerSortItemBinding>(
                viewBinding = { inflater, parent ->
                    PickerSortItemBinding.inflate(inflater, parent, false)
                }
            ) {
                bind {
                    binding.fieldTypeSpinner.setup(
                        items = item.fields.map { it.displayName },
                        selectedItem = item.field.displayName
                    ) { position ->
                        viewModel.onFieldSelected(
                            absoluteAdapterPosition,
                            item.fields.getOrNull(position)
                        )
                    }

                    binding.conditionSpinner.setup(
                        items = item.conditions.map { getString(it.displayNameResId) },
                        selectedItem = item.condition?.let { getString(it.displayNameResId) }
                    ) { position ->
                        viewModel.onConditionSelected(
                            absoluteAdapterPosition,
                            item.conditions.getOrNull(position)
                        )
                    }
                }

                onViewRecycled {
                    binding.fieldTypeSpinner.recycle()
                    binding.conditionSpinner.recycle()
                }
            }
        )

        binding.applyAction.setOnClickListener { viewModel.applySort() }
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

    data class Args(
        val entityTypeSchema: FiberyEntityTypeSchema,
        val sort: FiberyEntitySortData,
    )

    fun interface ArgsProvider {

        fun getPickerSortArgs(): Args
    }

    interface ParentListener {

        fun onSortSelected(sort: FiberyEntitySortData)

        fun onBackPressed()
    }
}
