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
package by.krossovochkin.fiberyunofficial.entitytypelist.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.initErrorHandler
import by.krossovochkin.fiberyunofficial.core.presentation.initNavigation
import by.krossovochkin.fiberyunofficial.core.presentation.initProgressBar
import by.krossovochkin.fiberyunofficial.core.presentation.initRecyclerView
import by.krossovochkin.fiberyunofficial.core.presentation.initToolbar
import by.krossovochkin.fiberyunofficial.core.presentation.parentListener
import by.krossovochkin.fiberyunofficial.core.presentation.setupTransformEnterTransition
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import by.krossovochkin.fiberyunofficial.entitytypelist.R
import by.krossovochkin.fiberyunofficial.entitytypelist.databinding.EntityTypeListFragmentBinding
import by.krossovochkin.fiberyunofficial.entitytypelist.databinding.EntityTypeListItemBinding
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

class EntityTypeListFragment(
    factoryProducer: () -> EntityTypeListViewModelFactory
) : Fragment(R.layout.entity_type_list_fragment) {

    private val viewModel: EntityTypeListViewModel by viewModels { factoryProducer() }

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
            toolbarData = MutableLiveData(viewModel.getToolbarViewState(requireContext())),
            onBackPressed = { viewModel.onBackPressed() }
        )

        initRecyclerView(
            recyclerView = binding.entityTypeListRecyclerView,
            itemsLiveData = viewModel.entityTypeItems,
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
                        .getString(R.string.entity_type_list_list_transition_name, adapterPosition)
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
