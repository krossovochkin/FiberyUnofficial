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

package com.krossovochkin.commentlist.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.krossovochkin.commentlist.R
import com.krossovochkin.commentlist.databinding.CommentListFragmentBinding
import com.krossovochkin.commentlist.databinding.CommentListItemBinding
import com.krossovochkin.core.presentation.animation.setupTransformEnterTransition
import com.krossovochkin.core.presentation.list.ListItem
import com.krossovochkin.core.presentation.navigation.initNavigation
import com.krossovochkin.core.presentation.paging.initPaginatedRecyclerView
import com.krossovochkin.core.presentation.result.parentListener
import com.krossovochkin.core.presentation.ui.error.initErrorHandler
import com.krossovochkin.core.presentation.ui.toolbar.initToolbar
import com.krossovochkin.core.presentation.viewbinding.viewBinding
import com.krossovochkin.fiberyunofficial.domain.FiberyEntityTypeSchema
import com.krossovochkin.fiberyunofficial.domain.ParentEntityData
import dagger.Lazy
import io.noties.markwon.Markwon
import kotlinx.coroutines.flow.MutableStateFlow

class CommentListFragment(
    viewModelFactory: Lazy<CommentListViewModelFactory>
) : Fragment(R.layout.comment_list_fragment) {

    private val viewModel: CommentListViewModel by viewModels { viewModelFactory.get() }

    private val binding by viewBinding(CommentListFragmentBinding::bind)

    private val parentListener: ParentListener by parentListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransformEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initNavigation(
            navigationData = viewModel.navigation,
            transitionName = requireContext().getString(R.string.comment_list_root_transition_name)
        ) { event ->
            when (event) {
                CommentListNavEvent.BackEvent -> parentListener.onBackPressed()
            }
        }

        initToolbar(
            toolbar = binding.commentListToolbar,
            toolbarData = MutableStateFlow(viewModel.toolbarViewState),
            onBackPressed = { viewModel.onBackPressed() }
        )

        initErrorHandler(viewModel.error)

        initPaginatedRecyclerView(
            recyclerView = binding.commentListRecyclerView,
            itemsFlow = viewModel.entityItems,
            diffCallback = object : DiffUtil.ItemCallback<ListItem>() {
                override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
                    return if (oldItem is CommentListItem && newItem is CommentListItem) {
                        oldItem.commentData.id == newItem.commentData.id
                    } else {
                        oldItem === newItem
                    }
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
                    return oldItem.equals(newItem)
                }
            },
            adapterDelegateViewBinding<CommentListItem, ListItem, CommentListItemBinding>(
                viewBinding = { inflater, parent ->
                    CommentListItemBinding.inflate(inflater, parent, false)
                }
            ) {
                bind {
                    binding.commentAuthorName.text = item.authorName
                    binding.commentCreateDate.text = item.createDate
                    Markwon.create(context).setMarkdown(binding.commentText, item.text)
                }
            }
        ) { error -> viewModel.onError(error) }
    }

    data class Args(
        val entityTypeSchema: FiberyEntityTypeSchema,
        val parentEntityData: ParentEntityData
    )

    fun interface ArgsProvider {

        fun getCommentListArgs(): Args
    }

    interface ParentListener {

        fun onBackPressed()
    }
}
