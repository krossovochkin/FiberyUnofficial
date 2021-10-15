/*
   Copyright 2020 Vasya Drobushkov

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package com.krossovochkin.core.presentation.animation

import android.graphics.Color
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import com.krossovochkin.core.presentation.color.ColorUtils

private const val TRANSITION_DURATION_MILLIS = 400L

fun Fragment.delayTransitions() {
    postponeEnterTransition()
    requireView().doOnPreDraw { startPostponedEnterTransition() }
}

fun Fragment.setupTransformExitTransition() {
    exitTransition = MaterialElevationScale(false).apply {
        duration = TRANSITION_DURATION_MILLIS
    }
    reenterTransition = MaterialElevationScale(true).apply {
        duration = TRANSITION_DURATION_MILLIS
    }
}

fun Fragment.setupTransformEnterTransition() {
    sharedElementEnterTransition = MaterialContainerTransform().apply {
        duration = TRANSITION_DURATION_MILLIS
        scrimColor = Color.TRANSPARENT
        setAllContainerColors(ColorUtils.getColor(requireContext(), R.attr.colorSurface))
    }
}
