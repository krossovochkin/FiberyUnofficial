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

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.initNavigation
import by.krossovochkin.fiberyunofficial.core.presentation.initRecyclerView
import by.krossovochkin.fiberyunofficial.core.presentation.initToolbar
import by.krossovochkin.fiberyunofficial.core.presentation.setupTransformEnterTransition
import by.krossovochkin.fiberyunofficial.core.presentation.updateInsetMargins
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.krossovochkin.fiberyunofficial.pickerfilter.R
import com.krossovochkin.fiberyunofficial.pickerfilter.databinding.PickerFilterFragmentBinding
import com.krossovochkin.fiberyunofficial.pickerfilter.databinding.PickerFilterItemEmptyBinding
import com.krossovochkin.fiberyunofficial.pickerfilter.databinding.PickerFilterItemSingleSelectBinding

class PickerFilterFragment(
    factoryProvider: () -> PickerFilterViewModelFactory
) : Fragment(R.layout.picker_filter_fragment) {

    private val viewModel: PickerFilterViewModel by viewModels { factoryProvider() }

    private val binding by viewBinding(PickerFilterFragmentBinding::bind)

    private var parentListener: ParentListener? = null

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
                    parentListener?.onFilterSelected(
                        filter = event.filter,
                        params = event.params
                    )
                }
                PickerFilterNavEvent.BackEvent -> parentListener?.onBackPressed()
            }
        }

        initToolbar(
            toolbar = binding.pickerFilterToolbar,
            toolbarData = MutableLiveData(viewModel.toolbarViewState),
            onBackPressed = { viewModel.onBackPressed() }
        )

        initRecyclerView(
            recyclerView = binding.recyclerView,
            itemsLiveData = viewModel.items,
            adapterDelegateViewBinding<EmptyFilterItem, ListItem, PickerFilterItemEmptyBinding>(
                viewBinding = { inflater, parent ->
                    PickerFilterItemEmptyBinding.inflate(inflater, parent, false)
                }
            ) {
                bind {
                    binding.spinner.setup(
                        items = item.fields.map { it.displayName }
                    ) { position ->
                        viewModel.onFieldSelected(adapterPosition, item.fields.getOrNull(position))
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
                            adapterPosition,
                            item.fields.getOrNull(position)
                        )
                    }

                    binding.conditionSpinner.setup(
                        items = item.conditions.map { it.displayText },
                        selectedItem = item.condition?.displayText
                    ) { position ->
                        viewModel.onConditionSelected(
                            adapterPosition,
                            item.conditions.getOrNull(position)
                        )
                    }

                    binding.singleSelectValueSpinner.setup(
                        items = item.values.map { it.title },
                        selectedItem = item.selectedValue?.title
                    ) { position ->
                        viewModel.onSingleSelectValueSelected(
                            adapterPosition,
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
        binding.applyAction.updateInsetMargins(requireActivity(), bottom = true)
    }

    private inline fun Spinner.setup(
        items: List<String>,
        selectedItem: String? = null,
        crossinline onSelection: (Int) -> Unit
    ) {
        this.adapter = ArrayAdapter<String>(
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
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                onSelection(position - 1)
            }
        }
    }

    private fun Spinner.recycle() {
        this.onItemSelectedListener = null
        this.adapter = null
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
        val entityTypeSchema: FiberyEntityTypeSchema,
        val filter: String,
        val params: String
    )

    fun interface ArgsProvider {

        fun getPickerFilterArgs(): Args
    }

    interface ParentListener {

        fun onFilterSelected(filter: String, params: String)

        fun onBackPressed()
    }
}
