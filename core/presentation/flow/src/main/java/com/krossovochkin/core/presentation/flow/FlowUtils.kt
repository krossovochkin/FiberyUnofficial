package com.krossovochkin.core.presentation.flow

import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

inline fun <reified T> Flow<T>.collect(
    fragment: Fragment,
    noinline collector: suspend (T) -> Unit
) {
    this.flowWithLifecycle(fragment.lifecycle)
        .onEach { collector(it) }
        .launchIn(fragment.lifecycleScope)
}
