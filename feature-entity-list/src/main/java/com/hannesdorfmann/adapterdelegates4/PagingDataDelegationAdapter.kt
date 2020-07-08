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
package com.hannesdorfmann.adapterdelegates4

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/*
Temporary implementation of delegation adapter before official support:
https://github.com/sockeqwe/AdapterDelegates/issues/88
 */
class PagingDataDelegationAdapter<T : Any>(
    diffCallback: DiffUtil.ItemCallback<T>,
    vararg delegates: AdapterDelegate<List<T>>
) : PagingDataAdapter<T, RecyclerView.ViewHolder>(diffCallback) {

    private val delegatesManager: AdapterDelegatesManager<List<T>> = AdapterDelegatesManager()

    init {
        delegates.forEach { delegatesManager.addDelegate(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegatesManager.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item =
            getItem(position) // Internally triggers loading items around items around the given position
        val delegate = delegatesManager.getDelegateForViewType(holder.itemViewType)
            ?: throw NullPointerException(
                "No delegate found for item at position = " +
                    position +
                    " for viewType = " +
                    holder.itemViewType
            )

        delegate.onBindViewHolder(listOf(item as T), 0, holder, emptyList())
    }

    override fun getItemViewType(position: Int): Int {
        return 0
//        return delegatesManager.getItemViewType(getCurrentList(), position)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        delegatesManager.onViewRecycled(holder)
    }

    override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        return delegatesManager.onFailedToRecycleView(holder)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        delegatesManager.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        delegatesManager.onViewDetachedFromWindow(holder)
    }
}
