package com.ayaan.bazaar.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val BlueWhiteColorScheme = lightColorScheme(
    primary = Blue500,
    onPrimary = SoftWhite,
    primaryContainer = Blue50,
    onPrimaryContainer = Slate900,
    secondary = Blue400,
    onSecondary = SoftWhite,
    secondaryContainer = Blue50,
    onSecondaryContainer = Slate900,
    tertiary = Blue600,
    onTertiary = SoftWhite,
    error = ErrorRed,
    onError = SoftWhite,
    errorContainer = Color(0xFFFFEDEA),
    onErrorContainer = Color(0xFF8B1F1F),
    background = SoftWhite,
    onBackground = Slate900,
    surface = SoftWhite,
    onSurface = Slate900,
    surfaceVariant = Slate200,
    onSurfaceVariant = Slate700,
    outline = Slate200,
    outlineVariant = Blue50,
    scrim = Color(0x66000000),
    inverseSurface = Slate900,
    inverseOnSurface = SoftWhite,
    inversePrimary = Blue400,
)

@Composable
fun BazaarTheme(
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

        darkTheme -> BlueWhiteColorScheme // Using light theme even for dark mode for consistent blue-white branding
        else -> BlueWhiteColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}