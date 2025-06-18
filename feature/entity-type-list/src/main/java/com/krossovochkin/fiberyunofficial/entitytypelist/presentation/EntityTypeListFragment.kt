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
package com.krossovochkin.fiberyunofficial.entitytypelist.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.krossovochkin.core.presentation.animation.setupTransformEnterTransition
import com.krossovochkin.core.presentation.color.ColorUtils
import com.krossovochkin.core.presentation.list.ListItem
import com.krossovochkin.core.presentation.list.initRecyclerView
import com.krossovochkin.core.presentation.navigation.initNavigation
import com.krossovochkin.core.presentation.result.parentListener
import com.krossovochkin.core.presentation.ui.error.initErrorHandler
import com.krossovochkin.core.presentation.ui.progress.initProgressBar
import com.krossovochkin.core.presentation.ui.toolbar.initToolbar
import com.krossovochkin.core.presentation.viewbinding.viewBinding
import com.krossovochkin.fiberyunofficial.domain.FiberyAppData
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.entitytypelist.R
import com.krossovochkin.fiberyunofficial.entitytypelist.databinding.EntityTypeListFragmentBinding
import com.krossovochkin.fiberyunofficial.entitytypelist.databinding.EntityTypeListItemBinding
import dagger.Lazy
import kotlinx.coroutines.flow.MutableStateFlow

class EntityTypeListFragment(
    viewModelFactory: EntityTypeListViewModel.Factory,
    argsProvider: ArgsProvider
) : Fragment(R.layout.entity_type_list_fragment) {

    private val viewModel: EntityTypeListViewModel by viewModels {
        EntityTypeListViewModel.provideFactory(
            viewModelFactory,
            argsProvider.getEntityTypeListArgs()
        )
    }

    private val binding by viewBinding(EntityTypeListFragmentBinding::bind)

    private val parentListener: ParentListener by parentListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransformEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initNavigation(
            navigationData = viewModel.navigation,
            transitionName = requireContext().getString(R.string.entity_type_list_root_transition_name)
        ) { event ->
            when (event) {
                is EntityTypeListNavEvent.OnEntityTypeSelectedEvent -> {
                    parentListener.onEntityTypeSelected(event.entityTypeSchema, event.itemView)
                }
                is EntityTypeListNavEvent.BackEvent -> {
                    parentListener.onBackPressed()
                }
            }
        }

        initToolbar(
            toolbar = binding.entityTypeListToolbar,
            toolbarData = MutableStateFlow(viewModel.getToolbarViewState(requireContext())),
            onBackPressed = { viewModel.onBackPressed() }
        )

        initRecyclerView(
            recyclerView = binding.entityTypeListRecyclerView,
            itemsFlow = viewModel.entityTypeItems,
            adapterDelegateViewBinding<EntityTypeListItem, ListItem, EntityTypeListItemBinding>(
                viewBinding = { inflater, parent ->
                    EntityTypeListItemBinding.inflate(inflater, parent, false)
                }
            ) {
                bind {
                    itemView.setOnClickListener { viewModel.select(item, itemView) }
                    binding.entityTypeTitleTextView.text = item.title
                    binding.entityTypeBadgeView.setBackgroundColor(
                        ColorUtils.getDesaturatedColorIfNeeded(requireContext(), item.badgeBgColor)
                    )
                    itemView.transitionName = requireContext()
                        .getString(
                            R.string.entity_type_list_list_transition_name,
                            absoluteAdapterPosition
                        )
                }
            }
        )

        initProgressBar(
            progressBar = binding.progressBar,
            progressVisibleData = viewModel.progress
        )

        initErrorHandler(viewModel.error)
    }

    data class Args(
        val fiberyAppData: FiberyAppData
    )

    fun interface ArgsProvider {

        fun getEntityTypeListArgs(): Args
    }

    interface ParentListener {

        fun onEntityTypeSelected(entityTypeSchema: FiberyEntityTypeSchema, itemView: View)

        fun onBackPressed()
    }
}
