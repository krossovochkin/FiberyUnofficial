package com.krossovochkin.core.presentation.system

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import com.krossovochkin.core.presentation.color.ColorUtils

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
    activity: Activity,
    top: Boolean = false,
    bottom: Boolean = false
) {
    doOnPreDraw {
        val insets = ViewCompat.getRootWindowInsets(activity.window.decorView)
            ?.getInsets(WindowInsetsCompat.Type.systemBars())
        this.updateLayoutParams<ConstraintLayout.LayoutParams> {
            if (top) {
                updateMargins(top = insets?.top ?: 0)
            }
            if (bottom) {
                updateMargins(bottom = insets?.bottom ?: 0)
            }
        }
    }
}

fun View.updateInsetPaddings(
    bottom: Boolean = false
) {
    val originalPaddingBottom = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        if (bottom) {
            v.updatePadding(bottom = originalPaddingBottom + systemInsets.bottom)
        }

        insets
    }
    requestApplyInsets()
}
