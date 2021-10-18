package com.krossovochkin.core.presentation.resources

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

sealed class NativeText {
    data class Simple(
        val text: String
    ) : NativeText()

    data class Resource(
        @StringRes val id: Int
    ) : NativeText()

    data class Plural(
        @PluralsRes val id: Int,
        val number: Int,
        val args: List<Any>
    ) : NativeText() {

        constructor(@PluralsRes id: Int, number: Int) : this(id, number, listOf(number))
    }

    data class Arguments(
        @StringRes val id: Int,
        val args: List<Any>
    ) : NativeText() {

        constructor(@StringRes id: Int, vararg args: Any) : this(id, args.toList())
    }

    data class Multi(
        val texts: List<NativeText>
    ) : NativeText()
}

fun Context.resolveNativeText(nativeText: NativeText): CharSequence {
    return when (nativeText) {
        is NativeText.Arguments -> this.getString(nativeText.id, *nativeText.args.toTypedArray())
        is NativeText.Multi -> buildString {
            nativeText.texts.forEach {
                append(resolveNativeText(it))
            }
        }
        is NativeText.Plural -> resources.getQuantityString(
            nativeText.id,
            nativeText.number,
            *nativeText.args.toTypedArray()
        )
        is NativeText.Resource -> getString(nativeText.id)
        is NativeText.Simple -> nativeText.text
    }
}
