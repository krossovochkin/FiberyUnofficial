package by.krossovochkin.fiberyunofficial.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseCallbackViewModel<T : Any> : ViewModel() {

    private val data = Channel<T>(Channel.BUFFERED)
    val observe: Flow<T>
        get() = data.receiveAsFlow()

    fun send(data: T) {
        viewModelScope.launch {
            this@BaseCallbackViewModel.data.send(data)
        }
    }
}
