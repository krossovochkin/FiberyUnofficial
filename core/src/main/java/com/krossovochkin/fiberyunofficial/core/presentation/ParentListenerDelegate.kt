package com.krossovochkin.fiberyunofficial.core.presentation

import androidx.fragment.app.Fragment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ParentListenerDelegate<T>(
    private val fragment: Fragment
) : ReadOnlyProperty<Fragment, T> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return fragment.context as T
    }
}

fun <T> Fragment.parentListener() = ParentListenerDelegate<T>(this)
