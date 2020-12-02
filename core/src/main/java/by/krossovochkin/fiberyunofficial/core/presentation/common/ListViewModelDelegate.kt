package by.krossovochkin.fiberyunofficial.core.presentation.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.krossovochkin.fiberyunofficial.core.presentation.Event
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.core.presentation.load

class ListViewModelDelegate(
    private val viewModel: ViewModel,
    private val mutableProgress: MutableLiveData<Boolean>,
    private val mutableError: MutableLiveData<Event<Exception>>,
    private val load: suspend () -> List<ListItem>
) {

    private val mutableItems = MutableLiveData<List<ListItem>>()
    val items: LiveData<List<ListItem>> = mutableItems

    init {
        loadInternal()
    }

    private fun loadInternal() {
        viewModel.load(
            mutableProgress = mutableProgress,
            mutableError = mutableError
        ) {
            mutableItems.value = load()
        }
    }

    fun invalidate() {
        loadInternal()
    }
}
