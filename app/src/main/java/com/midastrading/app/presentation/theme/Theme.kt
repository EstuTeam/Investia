package com.midastrading.app.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = MidasPrimary,
    onPrimary = Color.White,
    primaryContainer = MidasPrimaryDark,
    onPrimaryContainer = MidasPrimaryLight,

    secondary = MidasSecondary,
    onSecondary = Color.White,
    secondaryContainer = MidasSecondary.copy(alpha = 0.2f),
    onSecondaryContainer = MidasSecondary,

    tertiary = MidasAccent,
    onTertiary = Color.White,
    tertiaryContainer = MidasAccent.copy(alpha = 0.2f),
    onTertiaryContainer = MidasAccentLight,

    background = DarkBg,
    onBackground = TextPrimary,

    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextMuted,

    outline = DarkBorder,
    outlineVariant = DarkBorderLight,

    error = LossRed,
    onError = Color.White,
    errorContainer = LossRedBg,
    onErrorContainer = LossRedLight,

    inverseSurface = LightBg,
    inverseOnSurface = LightTextPrimary,
    inversePrimary = MidasPrimaryDark,

    scrim = Color.Black.copy(alpha = 0.6f)
)

private val LightColorScheme = lightColorScheme(
    primary = MidasPrimary,
    onPrimary = Color.White,
    primaryContainer = MidasPrimaryLight.copy(alpha = 0.15f),
    onPrimaryContainer = MidasPrimaryDark,

    secondary = MidasSecondary,
    onSecondary = Color.White,

    tertiary = MidasAccent,
    onTertiary = Color.White,

    background = LightBg,
    onBackground = LightTextPrimary,

    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,

    outline = Color(0xFFE2E8F0),
    outlineVariant = Color(0xFFF1F5F9),

    error = LossRed,
    onError = Color.White
)

@Composable
fun MidasTheme(
    darkTheme: Boolean = true, // Default dark like web
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = if (darkTheme) DarkBg.toArgb() else LightBg.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MidasTypography,
        content = content
    )
}
