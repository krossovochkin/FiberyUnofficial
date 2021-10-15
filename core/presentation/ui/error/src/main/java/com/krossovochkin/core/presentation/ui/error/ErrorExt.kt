package com.krossovochkin.core.presentation.ui.error

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.krossovochkin.core.presentation.flow.collect
import kotlinx.coroutines.flow.Flow

fun Fragment.initErrorHandler(
    errorData: Flow<Exception>
) {
    errorData.collect(this) { error ->
        Snackbar
            .make(
                requireView(),
                error.message ?: getString(R.string.unknown_error),
                Snackbar.LENGTH_SHORT
            )
            .show()
    }
}
