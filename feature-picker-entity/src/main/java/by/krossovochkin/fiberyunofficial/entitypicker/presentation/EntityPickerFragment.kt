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
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.ParentEntityData
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.initToolbar
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import by.krossovochkin.fiberyunofficial.entitypicker.DaggerEntityPickerComponent
import by.krossovochkin.fiberyunofficial.entitypicker.EntityPickerParentComponent
import by.krossovochkin.fiberyunofficial.entitypicker.R
import by.krossovochkin.fiberyunofficial.entitypicker.databinding.FragmentEntityPickerBinding
import by.krossovochkin.fiberyunofficial.entitypicker.databinding.ItemEntityPickerBinding
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.hannesdorfmann.adapterdelegates4.paging.PagedListDelegationAdapter
import javax.inject.Inject

class EntityPickerFragment(
    private val entityPickerParentComponent: EntityPickerParentComponent
) : Fragment(R.layout.fragment_entity_picker) {

    @Inject
    lateinit var viewModel: EntityPickerViewModel

    private val binding by viewBinding(FragmentEntityPickerBinding::bind)

    private var parentListener: ParentListener? = null

    private val adapter = PagedListDelegationAdapter(
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
        adapterDelegateViewBinding<EntityPickerItem, ListItem, ItemEntityPickerBinding>(
            viewBinding = { inflater, parent ->
                ItemEntityPickerBinding.inflate(inflater, parent, false)
            }
        ) {
            bind {
                itemView.setOnClickListener { viewModel.select(item) }
                binding.entityTitleTextView.text = item.title
            }
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        DaggerEntityPickerComponent.factory()
            .create(
                entityPickerParentComponent = entityPickerParentComponent,
                fragment = this
            )
            .inject(this)

        initList()
        initNavigation()
        initToolbar()

        viewModel.entityCreateEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.entityCreateAction.isEnabled = isEnabled
        }
        binding.entityCreateAction.setOnClickListener { viewModel.createEntity() }
    }

    private fun initList() {
        binding.entityPickerRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.entityPickerRecyclerView.adapter = adapter
        binding.entityPickerRecyclerView
            .addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))

        viewModel.entityItems.observe(viewLifecycleOwner) {
            adapter.submitList(it)
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