package io.github.husseinfo.maticonsearch

import android.content.Context
import android.util.TypedValue
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

fun getAppColorScheme(context: Context, darkTheme: Boolean): ColorScheme {
    val primaryColor = TypedValue()
    val secondaryColor = TypedValue()
    val accentColor = TypedValue()
    context.theme.resolveAttribute(R.attr.colorPrimary, primaryColor, true);
    context.theme.resolveAttribute(R.attr.colorSecondary, secondaryColor, true);
    context.theme.resolveAttribute(R.attr.colorAccent, accentColor, true);

    return if (darkTheme)
        darkColorScheme(
            primary = Color(primaryColor.data),
            secondary = Color(secondaryColor.data),
        )
    else
        lightColorScheme(
            primary = Color(primaryColor.data),
            secondary = Color(secondaryColor.data),
        )
}
