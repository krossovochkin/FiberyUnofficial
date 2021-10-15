package com.krossovochkin.core.presentation.ui.fab

import android.content.Context
import android.content.res.ColorStateList
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.krossovochkin.core.presentation.color.ColorUtils
import com.krossovochkin.core.presentation.system.updateInsetMargins

inline fun Fragment.initFab(
    fab: FloatingActionButton,
    state: FabViewState,
    transitionName: String,
    crossinline onClick: () -> Unit
) {
    fab.initFab(
        context = requireContext(),
        state = state,
        onClick = onClick
    )
    fab.updateInsetMargins(requireActivity(), bottom = true)
    fab.transitionName = transitionName
}

@PublishedApi
internal inline fun FloatingActionButton.initFab(
    context: Context,
    state: FabViewState,
    crossinline onClick: () -> Unit
) {
    this.backgroundTintList = ColorStateList.valueOf(
        ColorUtils.getDesaturatedColorIfNeeded(context, state.bgColorInt)
    )
    this.imageTintList = ColorStateList.valueOf(ColorUtils.getContrastColor(state.bgColorInt))

    this.setOnClickListener { onClick() }
}
