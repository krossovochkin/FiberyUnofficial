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

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.delayTransitions
import by.krossovochkin.fiberyunofficial.core.presentation.initToolbar
import by.krossovochkin.fiberyunofficial.core.presentation.setupTransformEnterTransition
import by.krossovochkin.fiberyunofficial.core.presentation.setupTransformExitTransition
import by.krossovochkin.fiberyunofficial.core.presentation.updateInsetPaddings
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import by.krossovochkin.fiberyunofficial.entitytypelist.R
import by.krossovochkin.fiberyunofficial.entitytypelist.databinding.EntityTypeListFragmentBinding
import by.krossovochkin.fiberyunofficial.entitytypelist.databinding.EntityTypeListItemBinding
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

class EntityTypeListFragment(
    factoryProducer: () -> EntityTypeListViewModelFactory
) : Fragment(R.layout.entity_type_list_fragment) {

    private val viewModel: EntityTypeListViewModel by viewModels { factoryProducer() }

    private val binding by viewBinding(EntityTypeListFragmentBinding::bind)

    private var parentListener: ParentListener? = null

    private val adapter = ListDelegationAdapter(
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransformEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        delayTransitions()

        view.transitionName = requireContext().getString(R.string.entity_type_list_root_transition_name)

        binding.entityTypeListRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.entityTypeListRecyclerView.adapter = adapter
        binding.entityTypeListRecyclerView
            .addItemDecoration(
                DividerItemDecoration(context, LinearLayout.VERTICAL)
            )
        binding.entityTypeListRecyclerView.updateInsetPaddings(bottom = true)

        viewModel.entityTypeItems.observe(viewLifecycleOwner) {
            adapter.items = it
            adapter.notifyDataSetChanged()
        }

        viewModel.progress.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
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

        viewModel.navigation.observe(viewLifecycleOwner) { event ->
            when (val navEvent = event.getContentIfNotHandled()) {
                is EntityTypeListNavEvent.OnEntityTypeSelectedEvent -> {
                    setupTransformExitTransition()
                    parentListener?.onEntityTypeSelected(navEvent.entityTypeSchema, navEvent.itemView)
                }
                is EntityTypeListNavEvent.BackEvent -> {
                    parentListener?.onBackPressed()
                }
            }
        }

        binding.entityTypeListToolbar.initToolbar(
            activity = requireActivity(),
            state = viewModel.getToolbarViewState(requireContext()),
            onBackPressed = { viewModel.onBackPressed() }
        )
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
