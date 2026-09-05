package com.krossovochkin.fiberyunofficial.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FiberyPrimary = Color(0xFFFDD965)
private val FiberyPrimaryVariant = Color(0xFFFDD965)

private val FiberyLightColorScheme = lightColorScheme(
    primary = FiberyPrimary,
    onPrimary = Color.Black,
    primaryContainer = FiberyPrimaryVariant,
)

private val FiberyDarkColorScheme = darkColorScheme(
    primary = FiberyPrimary,
    onPrimary = Color.Black,
    primaryContainer = FiberyPrimaryVariant,
)

@Composable
fun FiberyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) FiberyDarkColorScheme else FiberyLightColorScheme,
        content = content
    )
}
