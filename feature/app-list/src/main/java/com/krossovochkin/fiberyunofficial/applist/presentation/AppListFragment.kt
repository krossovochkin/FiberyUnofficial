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
package com.krossovochkin.fiberyunofficial.applist.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.krossovochkin.core.presentation.list.ListItem
import com.krossovochkin.core.presentation.list.initRecyclerView
import com.krossovochkin.core.presentation.navigation.initNavigation
import com.krossovochkin.core.presentation.result.parentListener
import com.krossovochkin.core.presentation.ui.error.initErrorHandler
import com.krossovochkin.core.presentation.ui.progress.initProgressBar
import com.krossovochkin.core.presentation.ui.toolbar.initToolbar
import com.krossovochkin.core.presentation.viewbinding.viewBinding
import com.krossovochkin.fiberyunofficial.applist.R
import com.krossovochkin.fiberyunofficial.applist.databinding.AppListFragmentBinding
import com.krossovochkin.fiberyunofficial.applist.databinding.AppListItemBinding
import com.krossovochkin.fiberyunofficial.domain.FiberyAppData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

@AndroidEntryPoint
class AppListFragment : Fragment(R.layout.app_list_fragment) {

    private val viewModel: AppListViewModel by viewModels()

    private val parentListener: ParentListener by parentListener()

    private val binding by viewBinding(AppListFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initNavigation(
            navigationData = viewModel.navigation
        ) { event ->
            when (event) {
                is AppListNavEvent.OnAppSelectedEvent -> {
                    parentListener.onAppSelected(event.fiberyAppData, event.itemView)
                }
            }
        }

        initToolbar(
            toolbar = binding.appListToolbar,
            toolbarData = MutableStateFlow(viewModel.getToolbarViewState())
        )

        initRecyclerView(
            recyclerView = binding.appListRecyclerView,
            itemsFlow = viewModel.appItems,
            adapterDelegateViewBinding<AppListItem, ListItem, AppListItemBinding>(
                viewBinding = { inflater, parent -> AppListItemBinding.inflate(inflater, parent, false) }
            ) {
                bind {
                    itemView.setOnClickListener { viewModel.select(item, itemView) }
                    binding.appTitleTextView.text = item.title
                    itemView.transitionName = requireContext()
                        .getString(R.string.app_list_list_transition_name, absoluteAdapterPosition)
                }
            }
        )

        initProgressBar(
            progressBar = binding.progressBar,
            progressVisibleData = viewModel.progress
        )

        initErrorHandler(viewModel.error)
    }

    interface ParentListener {

        fun onAppSelected(fiberyAppData: FiberyAppData, itemView: View)
    }
}
