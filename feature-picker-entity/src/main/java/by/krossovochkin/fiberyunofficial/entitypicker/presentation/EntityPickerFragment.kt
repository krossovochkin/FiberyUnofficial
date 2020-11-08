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
package by.krossovochkin.fiberyunofficial.entitypicker.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.ParentEntityData
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.delayTransitions
import by.krossovochkin.fiberyunofficial.core.presentation.initToolbar
import by.krossovochkin.fiberyunofficial.core.presentation.setupTransformEnterTransition
import by.krossovochkin.fiberyunofficial.core.presentation.updateInsetMargins
import by.krossovochkin.fiberyunofficial.core.presentation.updateInsetPaddings
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import by.krossovochkin.fiberyunofficial.entitypicker.DaggerEntityPickerComponent
import by.krossovochkin.fiberyunofficial.entitypicker.EntityPickerParentComponent
import by.krossovochkin.fiberyunofficial.entitypicker.R
import by.krossovochkin.fiberyunofficial.entitypicker.databinding.PickerEntityFragmentBinding
import by.krossovochkin.fiberyunofficial.entitypicker.databinding.PickerEntityItemBinding
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.PagingDataDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class EntityPickerFragment(
    private val entityPickerParentComponent: EntityPickerParentComponent
) : Fragment(R.layout.picker_entity_fragment) {

    @Inject
    lateinit var viewModel: EntityPickerViewModel

    private val binding by viewBinding(PickerEntityFragmentBinding::bind)

    private var parentListener: ParentListener? = null

    private val adapter =
        PagingDataDelegationAdapter(
            object : DiffUtil.ItemCallback<ListItem>() {
                override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
                    return if (oldItem is EntityPickerItem && newItem is EntityPickerItem) {
                        oldItem.entityData.id == newItem.entityData.id
                    } else {
                        oldItem === newItem
                    }
                }

                override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
                    return oldItem.equals(newItem)
                }
            },
            adapterDelegateViewBinding<EntityPickerItem, ListItem, PickerEntityItemBinding>(
                viewBinding = { inflater, parent ->
                    PickerEntityItemBinding.inflate(inflater, parent, false)
                }
            ) {
                bind {
                    itemView.setOnClickListener { viewModel.select(item) }
                    binding.entityTitleTextView.text = item.title
                }
            }
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransformEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        delayTransitions()

        DaggerEntityPickerComponent.factory()
            .create(
                entityPickerParentComponent = entityPickerParentComponent,
                fragment = this
            )
            .inject(this)

        view.transitionName = requireContext().getString(R.string.picker_entity_root_transition_name)

        initList()
        initNavigation()
        initToolbar()

        viewModel.entityCreateEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.entityCreateAction.isEnabled = isEnabled
        }
        binding.entityCreateAction.setOnClickListener { viewModel.createEntity() }
        binding.entityCreateAction.updateInsetMargins(requireActivity(), bottom = true)
    }

    private fun initList() {
        binding.entityPickerRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.entityPickerRecyclerView.adapter = adapter
        binding.entityPickerRecyclerView
            .addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
        binding.entityPickerRecyclerView.updateInsetPaddings(bottom = true)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.entityItems.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { error ->
                Snackbar
                    .make(
                        requireView(),
                        error.message ?: getString(R.string.unknown_error),
                        Snackbar.LENGTH_SHORT
                    )
                    .show()
            }
        }
    }

    private fun initNavigation() {
        viewModel.navigation.observe(viewLifecycleOwner) { event ->
            when (val navEvent = event.getContentIfNotHandled()) {
                is EntityPickerNavEvent.OnEntityPickedEvent -> {
                    parentListener?.onEntityPicked(
                        entity = navEvent.entity,
                        parentEntityData = navEvent.parentEntityData
                    )
                }
                is EntityPickerNavEvent.BackEvent -> {
                    parentListener?.onBackPressed()
                }
            }
        }
    }

    private fun initToolbar() {
        viewModel.toolbarViewState.observe(viewLifecycleOwner) {
            binding.entityPickerToolbar.initToolbar(
                activity = requireActivity(),
                state = it,
                onBackPressed = { viewModel.onBackPressed() },
                onSearchQueryChanged = { query -> viewModel.onSearchQueryChanged(query) }
            )
            binding.entityPickerToolbar
        }
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
        val entity: FiberyEntityData?
    )

    interface ArgsProvider {

        fun getEntityPickerArgs(arguments: Bundle): Args
    }

    interface ParentListener {

        fun onEntityPicked(
            parentEntityData: ParentEntityData,
            entity: FiberyEntityData?
        )

        fun onBackPressed()
    }
}
