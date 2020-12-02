package by.krossovochkin.fiberyunofficial.core.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

inline fun ViewModel.load(
    mutableProgress: MutableLiveData<Boolean>,
    mutableError: MutableLiveData<Event<Exception>>,
    crossinline action: suspend () -> Unit
) {
    viewModelScope.launch {
        try {
            mutableProgress.value = true
            action()
        } catch (e: Exception) {
            mutableError.value = Event(e)
        } finally {
            mutableProgress.value = false
        }
    }
}
