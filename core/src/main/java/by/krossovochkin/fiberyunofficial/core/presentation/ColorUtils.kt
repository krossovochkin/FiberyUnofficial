/*
   Copyright 2020 Vasya Drobushkov

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package by.krossovochkin.fiberyunofficial.core.presentation

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils

private const val DARKEN_COLOR_RATIO = 0.8f
private const val DESATURATE_COLOR_RATIO = 0.6f

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
        val color = Color.parseColor(colorHex)
        return if (Color.alpha(color) == 0) {
            Color.WHITE
        } else {
            color
        }
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

    fun isDarkMode(context: Context): Boolean {
        val mode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return mode == Configuration.UI_MODE_NIGHT_YES
    }

    @ColorInt
    fun getDesaturatedColorIfNeeded(context: Context, @ColorInt color: Int): Int {
        return if (isDarkMode(context)) {
            getDesaturatedColor(color)
        } else {
            color
        }
    }

    @ColorInt
    fun getDefaultContrastColor(context: Context): Int {
        return if (isDarkMode(context)) Color.WHITE else Color.BLACK
    }

    @ColorInt
    fun getContrastColor(@ColorInt color: Int): Int {
        val whiteContrast = ColorUtils.calculateContrast(Color.WHITE, color)
        val blackContrast = ColorUtils.calculateContrast(Color.BLACK, color)

        return if (whiteContrast > blackContrast) Color.WHITE else Color.BLACK
    }
}
