package by.krossovochkin.fiberyunofficial.core.presentation

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FragmentViewBindingDelegate<T : ViewBinding>(
    val fragment: Fragment,
    val viewBindingFactory: (View) -> T
) : ReadOnlyProperty<Fragment, T> {
    private var _binding: T? = null

    init {
        fragment.lifecycle.addObserver(
            object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_CREATE) {
                        fragment.viewLifecycleOwnerLiveData.observe(
                            fragment,
                            Observer { viewLifecycleOwner ->
                                viewLifecycleOwner.lifecycle.addObserver(
                                    object : LifecycleEventObserver {
                                        override fun onStateChanged(
                                            source: LifecycleOwner,
                                            event: Lifecycle.Event
                                        ) {
                                            if (event == Lifecycle.Event.ON_DESTROY) {
                                                _binding = null
                                            }
                                        }
                                    }
                                )
                            }
                        )
                    }
                }
            }
        )
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val binding = _binding
        if (binding != null) {
            return binding
        }

        val lifecycle = fragment.viewLifecycleOwner.lifecycle

        check(lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            "Should not attempt to get bindings when Fragment views are destroyed."
        }

        return viewBindingFactory(thisRef.requireView()).also { _binding = it }
    }
}

fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) =
    FragmentViewBindingDelegate(this, viewBindingFactory)
