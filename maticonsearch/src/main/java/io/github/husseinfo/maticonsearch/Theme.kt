package io.github.husseinfo.maticonsearch

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val appDarkColorScheme = darkColorScheme(
    primary = Color.Black,
    secondary = Color.White
)

private val appLightColorScheme = lightColorScheme(
    primary = Color.White,
    secondary = Color.Black
)

fun getThemeColorScheme(darkTheme: Boolean): ColorScheme =
    if (darkTheme) appDarkColorScheme else appLightColorScheme
