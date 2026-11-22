package com.krossovochkin.core.presentation.ui.toolbar

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

/**
 * Single source of truth for toolbar content vs background contrast.
 *
 * Both the TopAppBar content (title, navigation and action icons) and the
 * status bar icons must use this so they stay in sync: dark content on light
 * backgrounds and vice versa.
 */
fun Color.toolbarContentColor(): Color {
    val bg = toArgb()
    val whiteContrast = ColorUtils.calculateContrast(android.graphics.Color.WHITE, bg)
    val blackContrast = ColorUtils.calculateContrast(android.graphics.Color.BLACK, bg)
    return if (whiteContrast > blackContrast) Color.White else Color.Black
}

/**
 * Maps to WindowInsetsControllerCompat.isAppearanceLightStatusBars:
 * true = dark icons (for light backgrounds), false = light icons.
 * Derived from the same [toolbarContentColor] so status bar stays in sync
 * with toolbar text/icons.
 */
fun Color.isLightStatusBars(): Boolean = toolbarContentColor() == Color.Black
