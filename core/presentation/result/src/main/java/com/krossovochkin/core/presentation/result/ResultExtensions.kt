package com.krossovochkin.core.presentation.result

import android.os.Build
import android.os.Bundle
import android.os.Parcelable

const val KEY_RESULT = "result"

inline fun <reified T : Parcelable> Bundle.toResultParcelable(): T {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            this.getParcelable(KEY_RESULT, T::class.java)!!
        }

        else -> {
            @Suppress("DEPRECATION")
            this.getParcelable(KEY_RESULT)!!
        }
    }
}

fun Parcelable.toResultBundle(): Bundle {
    return Bundle().apply {
        putParcelable(KEY_RESULT, this@toResultBundle)
    }
}
