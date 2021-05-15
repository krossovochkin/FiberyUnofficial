package com.krossovochkin.fiberyunofficial.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

inline fun ViewModel.load(
    progress: MutableStateFlow<Boolean>,
    error: Channel<Exception>,
    crossinline action: suspend () -> Unit
) {
    viewModelScope.launch {
        try {
            progress.value = true
            action()
        } catch (e: Exception) {
            error.send(e)
        } finally {
            progress.value = false
        }
    }
}
