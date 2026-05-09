package com.krossovochkin.core.presentation.result

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResultBus @Inject constructor() {

    private val _results = MutableSharedFlow<Any>()
    val results = _results.asSharedFlow()

    suspend fun sendResult(result: Any) {
        _results.emit(result)
    }
}
