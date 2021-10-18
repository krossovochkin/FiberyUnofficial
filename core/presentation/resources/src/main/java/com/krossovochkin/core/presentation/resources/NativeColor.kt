package com.krossovochkin.core.presentation.resources

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.krossovochkin.core.presentation.color.ColorUtils

sealed class NativeColor {
    data class Simple(
        @ColorInt val color: Int
    ) : NativeColor()

    data class Hex(
        val colorHex: String
    ) : NativeColor()

    data class Resource(
        @ColorRes val id: Int
    ) : NativeColor()

    data class Attribute(
        @AttrRes val id: Int
    ) : NativeColor()
}

@ColorInt
fun Context.resolveNativeColor(nativeColor: NativeColor): Int {
    return when (nativeColor) {
        is NativeColor.Simple -> nativeColor.color
        is NativeColor.Hex -> ColorUtils.getColor(nativeColor.colorHex)
        is NativeColor.Attribute -> ColorUtils.getColor(this, nativeColor.id)
        is NativeColor.Resource -> ContextCompat.getColor(this, nativeColor.id)
    }
}
