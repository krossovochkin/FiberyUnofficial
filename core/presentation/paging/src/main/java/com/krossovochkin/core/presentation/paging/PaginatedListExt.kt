package com.krossovochkin.core.presentation.paging

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.PagingDataDelegationAdapter
import com.krossovochkin.core.presentation.list.ListItem
import com.krossovochkin.core.presentation.system.updateInsetPaddings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

inline fun <T : ListItem> Fragment.initPaginatedRecyclerView(
    recyclerView: RecyclerView,
    itemsFlow: Flow<PagingData<T>>,
    diffCallback: DiffUtil.ItemCallback<T>,
    vararg adapterDelegates: AdapterDelegate<List<T>>,
    crossinline onError: (Exception) -> Unit
) {
    val adapter = PagingDataDelegationAdapter(
        diffCallback = diffCallback,
        *adapterDelegates
    )

    recyclerView.layoutManager = LinearLayoutManager(context)
    recyclerView.adapter = adapter
    recyclerView
        .addItemDecoration(DividerItemDecoration(context, VERTICAL))
    recyclerView.updateInsetPaddings(bottom = true)

    viewLifecycleOwner.lifecycleScope.launch {
        launch {
            itemsFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
        launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                val refreshState = loadStates.refresh
                if (refreshState is LoadState.Error) {
                    onError(Exception(refreshState.error.message, refreshState.error))
                }
            }
        }
    }
}
