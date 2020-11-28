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

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.core.domain.ParentEntityData
import by.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.initErrorHandler
import by.krossovochkin.fiberyunofficial.core.presentation.initNavigation
import by.krossovochkin.fiberyunofficial.core.presentation.initPaginatedRecyclerView
import by.krossovochkin.fiberyunofficial.core.presentation.initToolbar
import by.krossovochkin.fiberyunofficial.core.presentation.parentListener
import by.krossovochkin.fiberyunofficial.core.presentation.setupTransformEnterTransition
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.krossovochkin.filelist.R
import com.krossovochkin.filelist.databinding.FileListFragmentBinding
import com.krossovochkin.filelist.databinding.FileListItemBinding

class FileListFragment(
    factoryProducer: () -> FileListViewModelFactory
) : Fragment(R.layout.file_list_fragment) {

    private val viewModel: FileListViewModel by viewModels { factoryProducer() }

    private val binding by viewBinding(FileListFragmentBinding::bind)

    private val parentListener: ParentListener by parentListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransformEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initNavigation(
            navigationData = viewModel.navigation,
            transitionName = requireContext().getString(R.string.file_list_root_transition_name)
        ) { event ->
            when (event) {
                FileListNavEvent.BackEvent -> parentListener.onBackPressed()
            }
        }

        initToolbar(
            toolbar = binding.fileListToolbar,
            toolbarData = MutableLiveData(viewModel.toolbarViewState),
            onBackPressed = { viewModel.onBackPressed() }
        )

        initErrorHandler(viewModel.error)

        initPaginatedRecyclerView(
            recyclerView = binding.fileListRecyclerView,
            itemsFlow = viewModel.entityItems,
            diffCallback = object : DiffUtil.ItemCallback<ListItem>() {
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
        ) { error -> viewModel.onError(error) }
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
