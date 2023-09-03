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

package com.krossovochkin.fiberyunofficial.entitylist.presentation

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.krossovochkin.core.presentation.animation.setupTransformEnterTransition
import com.krossovochkin.core.presentation.color.ColorUtils
import com.krossovochkin.core.presentation.list.ListItem
import com.krossovochkin.core.presentation.navigation.initNavigation
import com.krossovochkin.core.presentation.paging.initPaginatedRecyclerView
import com.krossovochkin.core.presentation.result.parentListener
import com.krossovochkin.core.presentation.result.toResultParcelable
import com.krossovochkin.core.presentation.ui.error.initErrorHandler
import com.krossovochkin.core.presentation.ui.fab.initFab
import com.krossovochkin.core.presentation.ui.toolbar.initToolbar
import com.krossovochkin.core.presentation.viewbinding.viewBinding
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityFilterData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntitySortData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData
import com.krossovochkin.fiberyunofficial.entitylist.R
import com.krossovochkin.fiberyunofficial.entitylist.databinding.EntityListFragmentBinding
import com.krossovochkin.fiberyunofficial.entitylist.databinding.EntityListItemBinding
import kotlinx.coroutines.flow.MutableStateFlow

class EntityListFragment(
    factoryProvider: () -> EntityListViewModelFactory
) : Fragment(R.layout.entity_list_fragment) {

    private val viewModel: EntityListViewModel by viewModels { factoryProvider() }

    private val binding by viewBinding(EntityListFragmentBinding::bind)

    private val parentListener: ParentListener by parentListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransformEnterTransition()

        setFragmentResultListener(RESULT_KEY_FILTER_PICKED) { _, bundle ->
            val data = bundle.toResultParcelable<FilterPickedData>()
            viewModel.onFilterSelected(filter = data.filter)
        }
        setFragmentResultListener(RESULT_KEY_ENTITY_CREATED) { _, bundle ->
            val data = bundle.toResultParcelable<EntityCreatedData>()
            viewModel.onEntityCreated(createdEntity = data.createdEntity)
        }
        setFragmentResultListener(RESULT_KEY_SORT_PICKED) { _, bundle ->
            val data = bundle.toResultParcelable<SortPickedData>()
            viewModel.onSortSelected(sort = data.sort)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initNavigation(
            navigationData = viewModel.navigation,
            transitionName = requireContext().getString(R.string.entity_list_root_transition_name)
        ) { event ->
            when (event) {
                is EntityListNavEvent.OnEntitySelectedEvent -> {
                    parentListener.onEntitySelected(event.entity, event.itemView)
                }
                is EntityListNavEvent.BackEvent -> {
                    parentListener.onBackPressed()
                }
                is EntityListNavEvent.OnFilterSelectedEvent -> {
                    parentListener.onFilterEdit(
                        entityTypeSchema = event.entityTypeSchema,
                        filter = event.filter,
                        view = event.view
                    )
                }
                is EntityListNavEvent.OnSortSelectedEvent -> {
                    parentListener.onSortEdit(
                        entityTypeSchema = event.entityTypeSchema,
                        sort = event.sort,
                        view = event.view
                    )
                }
                is EntityListNavEvent.OnCreateEntityEvent -> {
                    onCreateEntity(event.entityType, event.parentEntityData, event.view)
                }
            }
        }

        var filterView: View? = null
        var sortView: View? = null
        initToolbar(
            toolbar = binding.entityListToolbar,
            toolbarData = MutableStateFlow(viewModel.toolbarViewState),
            onBackPressed = { viewModel.onBackPressed() },
            onMenuItemClicked = { item ->
                when (item.itemId) {
                    R.id.action_filter -> {
                        viewModel.onFilterClicked(filterView!!)
                        true
                    }
                    R.id.action_sort -> {
                        viewModel.onSortClicked(sortView!!)
                        true
                    }
                    else -> error("Unknown menu item: $item")
                }
            },
            onToolbarUpdated = {
                filterView = requireView().findViewById(R.id.action_filter)
                filterView?.transitionName = requireContext()
                    .getString(R.string.entity_list_filter_transition_name)
                sortView = requireView().findViewById(R.id.action_sort)
                sortView?.transitionName = requireContext()
                    .getString(R.string.entity_list_sort_transition_name)
            }
        )

        initPaginatedRecyclerView(
            recyclerView = binding.entityListRecyclerView,
            itemsFlow = viewModel.entityItems,
            diffCallback = object : DiffUtil.ItemCallback<ListItem>() {
                override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
                    return if (oldItem is EntityListItem && newItem is EntityListItem) {
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
            adapterDelegateViewBinding<EntityListItem, ListItem, EntityListItemBinding>(
                viewBinding = { inflater, parent ->
                    EntityListItemBinding.inflate(inflater, parent, false)
                }
            ) {
                bind {
                    itemView.setOnClickListener { viewModel.select(item, itemView) }
                    binding.entityTitleTextView.text = item.title

                    binding.entityRemoveRelationAction.imageTintList =
                        ColorStateList.valueOf(ColorUtils.getDefaultContrastColor(requireContext()))
                    binding.entityRemoveRelationAction.isVisible = item.isRemoveAvailable
                    if (item.isRemoveAvailable) {
                        binding.entityRemoveRelationAction.setOnClickListener {
                            viewModel.removeRelation(item)
                        }
                    }

                    itemView.transitionName = requireContext()
                        .getString(
                            R.string.entity_list_list_transition_name,
                            absoluteAdapterPosition
                        )
                }
            },
            onError = viewModel::onError
        )

        initFab(
            fab = binding.entityListCreateFab,
            state = viewModel.getCreateFabViewState(requireContext()),
            transitionName = requireContext()
                .getString(R.string.entity_list_create_fab_transition_name)
        ) {
            viewModel.onCreateEntityClicked(binding.entityListCreateFab)
        }

        initErrorHandler(viewModel.error)
    }

    private fun onCreateEntity(
        entityType: FiberyEntityTypeSchema,
        parentEntityData: ParentEntityData?,
        view: View
    ) {
        parentListener.onAddEntityRequested(entityType, parentEntityData, view)
    }

    data class Args(
        val entityTypeSchema: FiberyEntityTypeSchema,
        val parentEntityData: ParentEntityData?
    )

    fun interface ArgsProvider {

        fun getEntityListArgs(): Args
    }

    interface ParentListener {

        fun onEntitySelected(entity: FiberyEntityData, itemView: View)

        fun onAddEntityRequested(
            entityType: FiberyEntityTypeSchema,
            parentEntityData: ParentEntityData?,
            view: View
        )

        fun onFilterEdit(
            entityTypeSchema: FiberyEntityTypeSchema,
            filter: FiberyEntityFilterData,
            view: View
        )

        fun onSortEdit(
            entityTypeSchema: FiberyEntityTypeSchema,
            sort: FiberyEntitySortData,
            view: View
        )

        fun onBackPressed()
    }
}
