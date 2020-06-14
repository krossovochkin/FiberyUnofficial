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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.initToolbar
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import by.krossovochkin.fiberyunofficial.entitytypelist.DaggerEntityTypeListComponent
import by.krossovochkin.fiberyunofficial.entitytypelist.EntityTypeListParentComponent
import by.krossovochkin.fiberyunofficial.entitytypelist.R
import by.krossovochkin.fiberyunofficial.entitytypelist.databinding.FragmentEntityTypeListBinding
import by.krossovochkin.fiberyunofficial.entitytypelist.databinding.ItemEntityTypeBinding
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import javax.inject.Inject

class EntityTypeListFragment(
    private val entityTypeListParentComponent: EntityTypeListParentComponent
) : Fragment(R.layout.fragment_entity_type_list) {

    @Inject
    lateinit var viewModel: EntityTypeListViewModel

    private val binding by viewBinding(FragmentEntityTypeListBinding::bind)

    private var parentListener: ParentListener? = null

    private val adapter = ListDelegationAdapter(
        adapterDelegateViewBinding<EntityTypeListItem, ListItem, ItemEntityTypeBinding>(
            viewBinding = { inflater, parent ->
                ItemEntityTypeBinding.inflate(inflater, parent, false)
            }
        ) {
            bind {
                itemView.setOnClickListener { viewModel.select(item) }
                binding.entityTypeTitleTextView.text = item.title
                binding.entityTypeBadgeView.setBackgroundColor(
                    ColorUtils.getDesaturatedColorIfNeeded(requireContext(), item.badgeBgColor)
                )
            }
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        DaggerEntityTypeListComponent.factory()
            .create(
                entityTypeListParentComponent = entityTypeListParentComponent,
                fragment = this
            )
            .inject(this)

        binding.entityTypeListRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.entityTypeListRecyclerView.adapter = adapter
        binding.entityTypeListRecyclerView
            .addItemDecoration(
                DividerItemDecoration(context, LinearLayout.VERTICAL)
            )

        viewModel.entityTypeItems.observe(viewLifecycleOwner, Observer {
            adapter.items = it
            adapter.notifyDataSetChanged()
        })

        viewModel.progress.observe(viewLifecycleOwner, Observer {
            binding.progressBar.isVisible = it
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { error ->
                Snackbar
                    .make(
                        requireView(),
                        error.message ?: getString(R.string.unknown_error),
                        Snackbar.LENGTH_SHORT
                    )
                    .show()
            }
        })

        viewModel.navigation.observe(viewLifecycleOwner, Observer { event ->
            when (val navEvent = event.getContentIfNotHandled()) {
                is EntityTypeListNavEvent.OnEntityTypeSelectedEvent -> {
                    parentListener?.onEntityTypeSelected(navEvent.entityTypeSchema)
                }
                is EntityTypeListNavEvent.BackEvent -> {
                    parentListener?.onBackPressed()
                }
            }
        })

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

    interface ArgsProvider {

        fun getEntityTypeListArgs(arguments: Bundle): Args
    }

    interface ParentListener {

        fun onEntityTypeSelected(entityTypeSchema: FiberyEntityTypeSchema)

        fun onBackPressed()
    }
}
