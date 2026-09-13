package com.krossovochkin.core.presentation.resources

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource

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

@Composable
fun NativeText.resolve(): String {
    return when (this) {
        is NativeText.Simple -> text
        is NativeText.Resource -> stringResource(id)
        is NativeText.Arguments -> stringResource(id, *args.toTypedArray())
        is NativeText.Plural -> pluralStringResource(id, number, *args.toTypedArray())
        is NativeText.Multi -> {
            val builder = StringBuilder()
            for (text in texts) {
                builder.append(text.resolve())
            }
            builder.toString()
        }
    }
}
