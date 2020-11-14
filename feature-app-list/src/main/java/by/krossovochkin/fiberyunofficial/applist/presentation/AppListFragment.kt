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

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.applist.R
import by.krossovochkin.fiberyunofficial.applist.databinding.AppListFragmentBinding
import by.krossovochkin.fiberyunofficial.applist.databinding.AppListItemBinding
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.core.presentation.Event
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.delayTransitions
import by.krossovochkin.fiberyunofficial.core.presentation.initToolbar
import by.krossovochkin.fiberyunofficial.core.presentation.setupTransformExitTransition
import by.krossovochkin.fiberyunofficial.core.presentation.updateInsetPaddings
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

class AppListFragment(
    factoryProducer: () -> AppListViewModelFactory
) : Fragment(R.layout.app_list_fragment) {

    private val viewModel: AppListViewModel by viewModels { factoryProducer() }

    private var parentListener: ParentListener? = null

    private val binding by viewBinding(AppListFragmentBinding::bind)

    private val adapter = ListDelegationAdapter(
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        delayTransitions()

        binding.appListRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.appListRecyclerView.adapter = adapter
        binding.appListRecyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayout.VERTICAL
            )
        )
        binding.appListRecyclerView.updateInsetPaddings(bottom = true)

        viewModel.appItems.observe(viewLifecycleOwner) {
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

        viewModel.navigation.observe(viewLifecycleOwner) { event: Event<AppListNavEvent> ->
            when (val navEvent = event.getContentIfNotHandled()) {
                is AppListNavEvent.OnAppSelectedEvent -> {
                    setupTransformExitTransition()
                    parentListener?.onAppSelected(navEvent.fiberyAppData, navEvent.itemView)
                }
            }
        }

        binding.appListToolbar.initToolbar(
            activity = requireActivity(),
            state = viewModel.getToolbarViewState(requireContext())
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

    interface ParentListener {

        fun onAppSelected(fiberyAppData: FiberyAppData, itemView: View)
    }
}
