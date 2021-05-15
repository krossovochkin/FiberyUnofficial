package com.krossovochkin.fiberyunofficial.core.presentation.common

import androidx.lifecycle.ViewModel
import com.krossovochkin.fiberyunofficial.core.presentation.ListItem
import com.krossovochkin.fiberyunofficial.core.presentation.load
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow

class ListViewModelDelegate(
    private val viewModel: ViewModel,
    private val progress: MutableStateFlow<Boolean>,
    private val errorChannel: Channel<Exception>,
    private val load: suspend () -> List<ListItem>
) {
    val items = MutableStateFlow(emptyList<ListItem>())

    init {
        loadInternal()
    }

    private fun loadInternal() {
        viewModel.load(
            progress = progress,
            error = errorChannel
        ) {
            items.value = load()
        }
    }

    fun invalidate() {
        loadInternal()
    }
}
