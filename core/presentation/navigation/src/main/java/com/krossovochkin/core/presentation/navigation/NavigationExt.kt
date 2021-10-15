package com.krossovochkin.core.presentation.navigation

import androidx.fragment.app.Fragment
import com.krossovochkin.core.presentation.animation.delayTransitions
import com.krossovochkin.core.presentation.animation.setupTransformExitTransition
import com.krossovochkin.core.presentation.flow.collect
import kotlinx.coroutines.flow.Flow

inline fun <reified T> Fragment.initNavigation(
    navigationData: Flow<T>,
    transitionName: String? = null,
    crossinline onEvent: (T) -> Unit
) {
    delayTransitions()

    transitionName?.let { requireView().transitionName = it }

    navigationData.collect(this) { event ->
        if (event != null) {
            setupTransformExitTransition()
            onEvent(event)
        }
    }
}
