package by.krossovochkin.fiberyunofficial.core.presentation

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils

private const val DARKEN_COLOR_RATIO = 0.8f
private const val DESATURATE_COLOR_RATIO = 0.6f
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

    @ColorInt
    fun getDesaturatedColor(@ColorInt color: Int): Int {
        val result = FloatArray(size = 3)
        Color.colorToHSV(color, result)
        result[1] *= DESATURATE_COLOR_RATIO
        return Color.HSVToColor(result)
    }

    fun isDarkColor(@ColorInt color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) < DARK_COLOR_LUMINANCE_THRESHOLD
    }

    fun Context.isDarkMode(): Boolean {
        val mode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return mode == Configuration.UI_MODE_NIGHT_YES
    }

    fun getDesaturatedColorIfNeeded(context: Context, @ColorInt color: Int): Int {
        return if (context.isDarkMode()) {
            getDesaturatedColor(color)
        } else {
            color
        }
    }

    fun getDefaultContrastColor(context: Context): Int {
        return ContextCompat.getColor(
            context,
            if (context.isDarkMode()) {
                android.R.color.white
            } else {
                android.R.color.black
            }
        )
    }

    fun getContrastColor(context: Context, @ColorInt color: Int): Int {
        return ContextCompat.getColor(
            context,
            if (isDarkColor(color)) {
                android.R.color.white
            } else {
                android.R.color.black
            }
        )
    }
}
