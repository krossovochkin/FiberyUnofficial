package com.krossovochkin.core.presentation.system

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import androidx.annotation.ColorInt
import com.krossovochkin.core.presentation.color.ColorUtils
import dev.chrisbanes.insetter.applyInsetter

@Suppress("DEPRECATION")
fun Activity.setupSystemBars(
    @ColorInt
    backgroundColor: Int,
    @ColorInt
    contrastColor: Int
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.setDecorFitsSystemWindows(false)
    } else {
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }

    this.window.navigationBarColor = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> Color.TRANSPARENT
        ColorUtils.isDarkMode(this) || Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> {
            Color.parseColor("#B3000000")
        }
        else -> Color.parseColor("#B3FFFFFF")
    }

    if (ColorUtils.isDarkMode(this)) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController
                ?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                )
        }
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController
                ?.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.window.decorView.systemUiVisibility = this.window.decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }

    this.window.statusBarColor = ColorUtils.getDarkenColor(backgroundColor)

    if (contrastColor == Color.WHITE) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController
                ?.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.window.decorView.systemUiVisibility = this.window.decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController
                ?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
        }
    }
}

fun View.updateInsetMargins(
    top: Boolean = false,
    bottom: Boolean = false
) {
    applyInsetter {
        type(statusBars = true) {
            margin(top = top)
        }
        type(navigationBars = true) {
            margin(bottom = bottom)
        }
    }
}

fun View.updateInsetPaddings(
    bottom: Boolean = false
) {
    applyInsetter {
        type(navigationBars = true) {
            padding(bottom = bottom)
        }
    }
}
