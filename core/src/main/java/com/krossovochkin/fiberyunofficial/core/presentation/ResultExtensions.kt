package com.krossovochkin.fiberyunofficial.core.presentation

import android.os.Bundle
import android.os.Parcelable

private const val KEY_RESULT = "result"

fun <T : Parcelable> Bundle.toResultParcelable(): T {
    return this.getParcelable(KEY_RESULT)!!
}

fun Parcelable.toResultBundle(): Bundle {
    return Bundle().apply {
        putParcelable(KEY_RESULT, this@toResultBundle)
    }
}
