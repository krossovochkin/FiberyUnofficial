package com.krossovochkin.core.presentation.ui.progress

import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.krossovochkin.core.presentation.flow.collect
import kotlinx.coroutines.flow.Flow

fun Fragment.initProgressBar(
    progressBar: View,
    progressVisibleData: Flow<Boolean>
) {
    progressVisibleData.collect(this) {
        progressBar.isVisible = it
    }
}
