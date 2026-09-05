package com.krossovochkin.core.presentation.resources

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.core.content.ContextCompat
import com.krossovochkin.core.presentation.color.ColorUtils

const val FIBERY_PRIMARY_HEX = "#FDDA65"

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
}

@ColorInt
fun Context.resolveNativeColor(nativeColor: NativeColor): Int {
    return when (nativeColor) {
        is NativeColor.Simple -> nativeColor.color
        is NativeColor.Hex -> ColorUtils.getColor(nativeColor.colorHex)
        is NativeColor.Resource -> ContextCompat.getColor(this, nativeColor.id)
    }
}

@Composable
fun NativeColor.toComposeColor(): Color {
    return when (this) {
        is NativeColor.Simple -> Color(color)
        is NativeColor.Hex -> Color(ColorUtils.getColor(colorHex))
        is NativeColor.Resource -> colorResource(id)
    }
}
