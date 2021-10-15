package com.krossovochkin.core.presentation.list

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.krossovochkin.core.presentation.flow.collect
import com.krossovochkin.core.presentation.system.updateInsetPaddings
import kotlinx.coroutines.flow.Flow

fun <T : ListItem> Fragment.initRecyclerView(
    recyclerView: RecyclerView,
    itemsFlow: Flow<List<T>>,
    vararg adapterDelegates: AdapterDelegate<List<T>>,
    itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(context, VERTICAL),
    diffCallback: DiffUtil.ItemCallback<T> = object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem.equals(newItem)
        }
    },
) {
    val adapter = ListDelegationAdapter(*adapterDelegates)
    val differ = AsyncListDiffer(adapter, diffCallback)

    recyclerView.layoutManager = LinearLayoutManager(context)
    recyclerView.adapter = adapter
    recyclerView.addItemDecoration(itemDecoration)
    recyclerView.updateInsetPaddings(bottom = true)

    itemsFlow.collect(this) {
        adapter.items = it
        differ.submitList(it)
    }
}
