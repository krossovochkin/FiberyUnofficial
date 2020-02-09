package by.krossovochkin.fiberyunofficial.core.presentation

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils

private const val DARKEN_COLOR_RATIO = 0.8f
private const val DARK_COLOR_LUMINANCE_THRESHOLD = 0.5

object ColorUtils {

    @ColorInt
    fun getColor(context: Context, @AttrRes attributeResId: Int): Int {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(attributeResId, typedValue, true)
        return typedValue.data
    }

    @ColorInt
    fun getColor(colorHex: String): Int {
        return Color.parseColor(colorHex)
    }

    @ColorInt
    fun getDarkenColor(@ColorInt color: Int): Int {
        val result = FloatArray(size = 3)
        Color.colorToHSV(color, result)
        result[2] *= DARKEN_COLOR_RATIO
        return Color.HSVToColor(result)
    }

    fun isDarkColor(@ColorInt color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) < DARK_COLOR_LUMINANCE_THRESHOLD
    }
}
