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

package by.krossovochkin.fiberyunofficial.entitylist.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.ParentEntityData
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.delayTransitions
import by.krossovochkin.fiberyunofficial.core.presentation.initFab
import by.krossovochkin.fiberyunofficial.core.presentation.initToolbar
import by.krossovochkin.fiberyunofficial.core.presentation.setupTransformEnterTransition
import by.krossovochkin.fiberyunofficial.core.presentation.setupTransformExitTransition
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import by.krossovochkin.fiberyunofficial.entitylist.DaggerEntityListComponent
import by.krossovochkin.fiberyunofficial.entitylist.EntityListParentComponent
import by.krossovochkin.fiberyunofficial.entitylist.R
import by.krossovochkin.fiberyunofficial.entitylist.databinding.DialogSortBinding
import by.krossovochkin.fiberyunofficial.entitylist.databinding.FragmentEntityListBinding
import by.krossovochkin.fiberyunofficial.entitylist.databinding.ItemEntityBinding
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.PagingDataDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class EntityListFragment(
    private val entityListParentComponent: EntityListParentComponent
) : Fragment(R.layout.fragment_entity_list) {

    @Inject
    lateinit var viewModel: EntityListViewModel

    private val binding by viewBinding(FragmentEntityListBinding::bind)

    private val entityCreatedViewModel by activityViewModels<EntityCreatedViewModel>()

    private var parentListener: ParentListener? = null

    private val adapter =
        PagingDataDelegationAdapter(
            object : DiffUtil.ItemCallback<ListItem>() {
                override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
                    return if (oldItem is EntityListItem && newItem is EntityListItem) {
                        oldItem.entityData.id == newItem.entityData.id
                    } else {
                        oldItem === newItem
                    }
                }

                override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
                    return oldItem.equals(newItem)
                }
            },
            adapterDelegateViewBinding<EntityListItem, ListItem, ItemEntityBinding>(
                viewBinding = { inflater, parent ->
                    ItemEntityBinding.inflate(inflater, parent, false)
                }
            ) {
                bind {
                    itemView.setOnClickListener { viewModel.select(item, itemView) }
                    binding.entityTitleTextView.text = item.title

                    binding.entityRemoveRelationAction.isVisible = item.isRemoveAvailable
                    if (item.isRemoveAvailable) {
                        binding.entityRemoveRelationAction.setOnClickListener {
                            viewModel.removeRelation(item)
                        }
                    }

                    itemView.transitionName = requireContext()
                        .getString(R.string.entity_list_list_transition_name, adapterPosition)
                }
            }
        )

    private val filterPickedViewModel: FilterPickedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransformEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        delayTransitions()

        DaggerEntityListComponent.factory()
            .create(
                entityListParentComponent = entityListParentComponent,
                fragment = this
            )
            .inject(this)

        view.transitionName = requireContext().getString(R.string.entity_list_root_transition_name)

        initList()
        initNavigation()
        initToolbar()

        filterPickedViewModel.pickedFilterSelect.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                viewModel.onFilterSelected(filter = it.filter, params = it.params)
            }
        }

        binding.entityListCreateFab.initFab(
            requireContext(),
            viewModel.getCreateFabViewState(requireContext())
        ) {
            viewModel.onCreateEntityClicked(binding.entityListCreateFab)
        }
        binding.entityListCreateFab.transitionName = requireContext()
            .getString(R.string.entity_list_create_fab_transition_name)

        entityCreatedViewModel.createdEntityId.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                viewModel.onEntityCreated(createdEntity = it.createdEntity)
            }
        }
    }

    private fun initList() {
        binding.entityListRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.entityListRecyclerView.adapter = adapter
        binding.entityListRecyclerView
            .addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))

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
                is EntityListNavEvent.OnEntitySelectedEvent -> {
                    setupTransformExitTransition()
                    parentListener?.onEntitySelected(navEvent.entity, navEvent.itemView)
                }
                is EntityListNavEvent.BackEvent -> {
                    parentListener?.onBackPressed()
                }
                is EntityListNavEvent.OnFilterSelectedEvent -> {
                    setupTransformExitTransition()
                    parentListener?.onFilterEdit(
                        entityTypeSchema = navEvent.entityTypeSchema,
                        filter = navEvent.filter,
                        params = navEvent.params,
                        view = navEvent.view
                    )
                }
                is EntityListNavEvent.OnSortSelectedEvent -> {
                    showUpdateSortDialog(navEvent.sort)
                }
                is EntityListNavEvent.OnCreateEntityEvent -> {
                    setupTransformExitTransition()
                    onCreateEntity(navEvent.entityType, navEvent.parentEntityData, navEvent.view)
                }
            }
        }
    }

    private fun initToolbar() {
        var filterView: View? = null

        val toolbarViewState = viewModel.toolbarViewState

        binding.entityListToolbar.initToolbar(
            activity = requireActivity(),
            state = toolbarViewState,
            onBackPressed = { viewModel.onBackPressed() },
            onMenuItemClicked = { item ->
                when (item.itemId) {
                    R.id.action_filter -> {
                        viewModel.onFilterClicked(filterView!!)
                        true
                    }
                    R.id.action_sort -> {
                        viewModel.onSortClicked()
                        true
                    }
                    else -> error("Unknown menu item: $item")
                }
            }
        )

        filterView = requireView().findViewById(R.id.action_filter)
        filterView?.transitionName = requireContext()
            .getString(R.string.entity_list_filter_transition_name)
        filterView.tag = toolbarViewState.bgColorInt
    }

    private fun showUpdateSortDialog(
        sort: String
    ) {
        val binding = DialogSortBinding.inflate(layoutInflater)
        binding.sortTextInput.setText(sort)

        AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setTitle(getString(R.string.dialog_sort_title))
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.onSortSelected(
                    sort = binding.sortTextInput.text.toString()
                )
            }
            .create()
            .show()
    }

    private fun onCreateEntity(
        entityType: FiberyEntityTypeSchema,
        parentEntityData: ParentEntityData?,
        view: View
    ) {
        parentListener?.onAddEntityRequested(entityType, parentEntityData, view)
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
        val parentEntityData: ParentEntityData?
    )

    interface ArgsProvider {

        fun getEntityListArgs(arguments: Bundle): Args
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
            filter: String,
            params: String,
            view: View
        )

        fun onBackPressed()
    }
}
