package by.krossovochkin.fiberyunofficial.core.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

inline fun <reified T> Flow<T>.collect(
    fragment: Fragment,
    noinline collector: suspend (T) -> Unit
) = FragmentFlowCollector(fragment, this, collector)

inline fun <reified T> Flow<T>.collectIn(
    fragment: Fragment
) = FragmentFlowCollector(fragment, this) {}

inline fun <reified T> Flow<T>.collectOnLifecycle(
    lifecycleOwner: LifecycleOwner,
    noinline collector: suspend (T) -> Unit
) = FlowCollector(lifecycleOwner, this, collector)

inline fun <reified T> Flow<T>.collectInLifecycle(
    lifecycleOwner: LifecycleOwner
) = FlowCollector(lifecycleOwner, this) {}

class FlowCollector<T>(
    lifecycleOwner: LifecycleOwner,
    private val flow: Flow<T>,
    private val collector: suspend (T) -> Unit
) {

    private var job: Job? = null

    init {
        lifecycleOwner.lifecycle.addObserver(
            LifecycleEventObserver { source: LifecycleOwner, event: Lifecycle.Event ->
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        job = source.lifecycleScope.launch {
                            flow.collect { collector(it) }
                        }
                    }
                    Lifecycle.Event.ON_STOP -> {
                        job?.cancel()
                        job = null
                    }
                    else -> Unit
                }
            }
        )
    }
}

class FragmentFlowCollector<T>(
    fragment: Fragment,
    private val flow: Flow<T>,
    private val collector: suspend (T) -> Unit
) {
    init {
        fragment.viewLifecycleOwnerLiveData.observe(fragment) { viewLifeCycleOwner ->
            FlowCollector(viewLifeCycleOwner, flow, collector)
        }
    }
}
