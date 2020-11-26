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

package com.krossovochkin.filelist.presentation

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.ParentEntityData
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.delayTransitions
import by.krossovochkin.fiberyunofficial.core.presentation.initToolbar
import by.krossovochkin.fiberyunofficial.core.presentation.setupTransformEnterTransition
import by.krossovochkin.fiberyunofficial.core.presentation.updateInsetPaddings
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.PagingDataDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.krossovochkin.filelist.R
import com.krossovochkin.filelist.databinding.FileListFragmentBinding
import com.krossovochkin.filelist.databinding.FileListItemBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FileListFragment(
    factoryProducer: () -> FileListViewModelFactory
) : Fragment(R.layout.file_list_fragment) {

    private val viewModel: FileListViewModel by viewModels { factoryProducer() }

    private val binding by viewBinding(FileListFragmentBinding::bind)

    private var parentListener: ParentListener? = null

    private val adapter =
        PagingDataDelegationAdapter(
            object : DiffUtil.ItemCallback<ListItem>() {
                override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
                    return if (oldItem is FileListItem && newItem is FileListItem) {
                        oldItem.fileData.id == newItem.fileData.id
                    } else {
                        oldItem === newItem
                    }
                }

                override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
                    return oldItem.equals(newItem)
                }
            },
            adapterDelegateViewBinding<FileListItem, ListItem, FileListItemBinding>(
                viewBinding = { inflater, parent ->
                    FileListItemBinding.inflate(inflater, parent, false)
                }
            ) {
                bind {
                    binding.fileTitleTextView.text = item.title

                    binding.fileDownloadAction.imageTintList =
                        ColorStateList.valueOf(ColorUtils.getDefaultContrastColor(requireContext()))
                    binding.fileDownloadAction.setOnClickListener {
                        viewModel.downloadFile(item.fileData)
                    }
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

        view.transitionName = requireContext().getString(R.string.file_list_root_transition_name)

        initList()
        initNavigation()
        initToolbar()
    }

    private fun initList() {
        binding.fileListRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.fileListRecyclerView.adapter = adapter
        binding.fileListRecyclerView
            .addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
        binding.fileListRecyclerView.updateInsetPaddings(bottom = true)

        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.entityItems.collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
            launch {
                adapter.loadStateFlow.collectLatest { loadStates ->
                    val refreshState = loadStates.refresh
                    if (refreshState is LoadState.Error) {
                        showError(refreshState.error.message)
                    }
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { error -> showError(error.message) }
        }
    }

    private fun showError(message: String?) {
        Snackbar
            .make(
                requireView(),
                message ?: getString(R.string.unknown_error),
                Snackbar.LENGTH_SHORT
            )
            .show()
    }

    private fun initNavigation() {
        viewModel.navigation.observe(viewLifecycleOwner) { event ->
            when (event.getContentIfNotHandled()) {
                FileListNavEvent.BackEvent -> parentListener?.onBackPressed()
            }
        }
    }

    private fun initToolbar() {
        binding.fileListToolbar.initToolbar(
            activity = requireActivity(),
            state = viewModel.toolbarViewState,
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
        val entityTypeSchema: FiberyEntityTypeSchema,
        val parentEntityData: ParentEntityData
    )

    fun interface ArgsProvider {

        fun getFileListArgs(): Args
    }

    interface ParentListener {

        fun onBackPressed()
    }
}
