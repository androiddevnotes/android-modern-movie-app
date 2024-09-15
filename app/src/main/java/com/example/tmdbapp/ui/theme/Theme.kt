package com.example.tmdbapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color(0xFF3F51B5),
    primaryVariant = Color(0xFF303F9F),
    secondary = Color(0xFFFF4081)
)

private val LightColorPalette = lightColors(
    primary = Color(0xFF3F51B5),
    primaryVariant = Color(0xFF303F9F),
    secondary = Color(0xFFFF4081)
)

@Composable
fun TMDBAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}