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
package com.krossovochkin.fiberyunofficial.entitypicker.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.krossovochkin.core.presentation.animation.setupTransformEnterTransition
import com.krossovochkin.core.presentation.flow.collect
import com.krossovochkin.core.presentation.list.ListItem
import com.krossovochkin.core.presentation.navigation.initNavigation
import com.krossovochkin.core.presentation.paging.initPaginatedRecyclerView
import com.krossovochkin.core.presentation.result.parentListener
import com.krossovochkin.core.presentation.system.updateInsetMargins
import com.krossovochkin.core.presentation.ui.error.initErrorHandler
import com.krossovochkin.core.presentation.ui.toolbar.initToolbar
import com.krossovochkin.core.presentation.viewbinding.viewBinding
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData
import com.krossovochkin.fiberyunofficial.entitypicker.R
import com.krossovochkin.fiberyunofficial.entitypicker.databinding.PickerEntityFragmentBinding
import com.krossovochkin.fiberyunofficial.entitypicker.databinding.PickerEntityItemBinding

class EntityPickerFragment(
    viewModelFactory: EntityPickerViewModel.Factory,
    argsProvider: ArgsProvider
) : Fragment(R.layout.picker_entity_fragment) {

    private val viewModel: EntityPickerViewModel by viewModels {
        EntityPickerViewModel.provideFactory(
            viewModelFactory,
            argsProvider.getEntityPickerArgs()
        )
    }

    private val binding by viewBinding(PickerEntityFragmentBinding::bind)

    private val parentListener: ParentListener by parentListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransformEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initNavigation(
            navigationData = viewModel.navigation,
            transitionName = requireContext().getString(R.string.picker_entity_root_transition_name)
        ) { event ->
            when (event) {
                is EntityPickerNavEvent.OnEntityPickedEvent -> {
                    parentListener.onEntityPicked(
                        entity = event.entity,
                        parentEntityData = event.parentEntityData
                    )
                }
                is EntityPickerNavEvent.BackEvent -> {
                    parentListener.onBackPressed()
                }
            }
        }

        initToolbar(
            toolbar = binding.entityPickerToolbar,
            toolbarData = viewModel.toolbarViewState,
            onBackPressed = { viewModel.onBackPressed() },
            onSearchQueryChanged = { query -> viewModel.onSearchQueryChanged(query) }
        )

        initPaginatedRecyclerView(
            recyclerView = binding.entityPickerRecyclerView,
            itemsFlow = viewModel.entityItems,
            diffCallback = object : DiffUtil.ItemCallback<ListItem>() {
                override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
                    return if (oldItem is EntityPickerItem && newItem is EntityPickerItem) {
                        oldItem.entityData.id == newItem.entityData.id
                    } else {
                        oldItem === newItem
                    }
                }

                @SuppressLint("DiffUtilEquals")
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
        ) { error -> viewModel.onError(error) }

        initErrorHandler(viewModel.error)

        viewModel.entityCreateEnabled.collect(this) { isEnabled ->
            binding.entityCreateAction.isEnabled = isEnabled
        }
        binding.entityCreateAction.setOnClickListener { viewModel.createEntity() }
        binding.entityCreateAction.updateInsetMargins(bottom = true)
    }

    data class Args(
        val parentEntityData: ParentEntityData,
        val entity: FiberyEntityData?
    )

    fun interface ArgsProvider {

        fun getEntityPickerArgs(): Args
    }

    interface ParentListener {

        fun onEntityPicked(
            parentEntityData: ParentEntityData,
            entity: FiberyEntityData?
        )

        fun onBackPressed()
    }
}
