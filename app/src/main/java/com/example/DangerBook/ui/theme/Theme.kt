package com.example.DangerBook.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DangerGoldLight,
    secondary = DangerGold,
    tertiary = DangerGoldDark,
    background = DangerGray,
    surface = DangerGrayLight,
    onPrimary = DangerBlack,
    onSecondary = DangerBlack,
    onTertiary = DangerWhite,
    onBackground = DangerWhite,
    onSurface = DangerWhite
)

private val LightColorScheme = lightColorScheme(
    primary = DangerGold,
    secondary = DangerGoldDark,
    tertiary = DangerGoldLight,
    background = DangerWhite,
    surface = DangerWhite,
    onPrimary = DangerBlack,
    onSecondary = DangerWhite,
    onTertiary = DangerBlack,
    onBackground = DangerBlack,
    onSurface = DangerBlack
)

@Composable
fun UINavegacionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}