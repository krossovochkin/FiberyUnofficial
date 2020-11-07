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
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.delayTransitions
import by.krossovochkin.fiberyunofficial.core.presentation.initToolbar
import by.krossovochkin.fiberyunofficial.core.presentation.setupTransformEnterTransition
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.krossovochkin.fiberyunofficial.pickerfilter.DaggerPickerFilterComponent
import com.krossovochkin.fiberyunofficial.pickerfilter.PickerFilterParentComponent
import com.krossovochkin.fiberyunofficial.pickerfilter.R
import com.krossovochkin.fiberyunofficial.pickerfilter.databinding.FragmentPickerFilterBinding
import com.krossovochkin.fiberyunofficial.pickerfilter.databinding.ItemFilterEmptyBinding
import com.krossovochkin.fiberyunofficial.pickerfilter.databinding.ItemFilterSingleSelectBinding
import javax.inject.Inject

class PickerFilterFragment(
    private val pickerFilterParentComponent: PickerFilterParentComponent
) : Fragment(R.layout.fragment_picker_filter) {

    @Inject
    lateinit var viewModel: PickerFilterViewModel

    private val binding by viewBinding(FragmentPickerFilterBinding::bind)

    private var parentListener: ParentListener? = null

    private val adapter = ListDelegationAdapter<List<ListItem>>(
        adapterDelegateViewBinding<EmptyFilterItem, ListItem, ItemFilterEmptyBinding>(
            viewBinding = { inflater, parent ->
                ItemFilterEmptyBinding.inflate(inflater, parent, false)
            }
        ) {
            bind {
                binding.spinner.setup(
                    items = item.fields.map { it.name }
                ) { position ->
                    viewModel.onFieldSelected(adapterPosition, item.fields.getOrNull(position))
                }
            }
            onViewRecycled { binding.spinner.recycle() }
        },
        adapterDelegateViewBinding<SingleSelectFilterItem, ListItem, ItemFilterSingleSelectBinding>(
            viewBinding = { inflater, parent ->
                ItemFilterSingleSelectBinding.inflate(inflater, parent, false)
            }
        ) {
            bind {
                binding.fieldTypeSpinner.setup(
                    items = item.fields.map { it.name },
                    selectedItem = item.field.name
                ) { position ->
                    viewModel.onFieldSelected(
                        adapterPosition,
                        item.fields.getOrNull(position)
                    )
                }

                binding.conditionSpinner.setup(
                    items = item.conditions.map { it.name },
                    selectedItem = item.condition?.name
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerPickerFilterComponent.factory()
            .create(
                pickerFilterParentComponent = pickerFilterParentComponent,
                fragment = this
            )
            .inject(this)

        setupTransformEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        delayTransitions()

        view.transitionName = requireContext()
            .getString(R.string.picker_filter_root_transition_name)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayout.VERTICAL
            )
        )

        binding.pickerFilterToolbar.initToolbar(
            activity = requireActivity(),
            state = viewModel.toolbarViewState,
            onBackPressed = { viewModel.onBackPressed() }
        )

        viewModel.navigation.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                when (it) {
                    is PickerFilterNavEvent.ApplyFilterEvent -> {
                        parentListener?.onFilterSelected(
                            filter = it.filter,
                            params = it.params
                        )
                    }
                    PickerFilterNavEvent.BackEvent -> parentListener?.onBackPressed()
                }
            }
        }

        viewModel.items.observe(viewLifecycleOwner) {
            adapter.items = it
            adapter.notifyDataSetChanged()
        }

        binding.applyAction.setOnClickListener { viewModel.applyFilter() }
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

    interface ArgsProvider {

        fun getPickerFilterArgs(arguments: Bundle): Args
    }

    interface ParentListener {

        fun onFilterSelected(filter: String, params: String)

        fun onBackPressed()
    }
}
