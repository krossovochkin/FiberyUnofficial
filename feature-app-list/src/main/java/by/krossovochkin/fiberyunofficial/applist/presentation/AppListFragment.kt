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
package by.krossovochkin.fiberyunofficial.applist.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import by.krossovochkin.fiberyunofficial.applist.R
import by.krossovochkin.fiberyunofficial.applist.databinding.AppListFragmentBinding
import by.krossovochkin.fiberyunofficial.applist.databinding.AppListItemBinding
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.initErrorHandler
import by.krossovochkin.fiberyunofficial.core.presentation.initNavigation
import by.krossovochkin.fiberyunofficial.core.presentation.initProgressBar
import by.krossovochkin.fiberyunofficial.core.presentation.initRecyclerView
import by.krossovochkin.fiberyunofficial.core.presentation.initToolbar
import by.krossovochkin.fiberyunofficial.core.presentation.parentListener
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

class AppListFragment(
    factoryProducer: () -> AppListViewModelFactory
) : Fragment(R.layout.app_list_fragment) {

    private val viewModel: AppListViewModel by viewModels { factoryProducer() }

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
            toolbarData = MutableLiveData(viewModel.getToolbarViewState(requireContext()))
        )

        initRecyclerView(
            recyclerView = binding.appListRecyclerView,
            itemsLiveData = viewModel.appItems,
            adapterDelegateViewBinding<AppListItem, ListItem, AppListItemBinding>(
                viewBinding = { inflater, parent -> AppListItemBinding.inflate(inflater, parent, false) }
            ) {
                bind {
                    itemView.setOnClickListener { viewModel.select(item, itemView) }
                    binding.appTitleTextView.text = item.title
                    itemView.transitionName = requireContext()
                        .getString(R.string.app_list_list_transition_name, adapterPosition)
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
