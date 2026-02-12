package com.investia.app.presentation.theme

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
    primary = InvestiaPrimary,
    onPrimary = Color.White,
    primaryContainer = InvestiaPrimaryDark,
    onPrimaryContainer = InvestiaPrimaryLight,

    secondary = InvestiaSecondary,
    onSecondary = Color.White,
    secondaryContainer = InvestiaSecondary.copy(alpha = 0.2f),
    onSecondaryContainer = InvestiaSecondary,

    tertiary = InvestiaAccent,
    onTertiary = Color.White,
    tertiaryContainer = InvestiaAccent.copy(alpha = 0.2f),
    onTertiaryContainer = InvestiaAccentLight,

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
    inversePrimary = InvestiaPrimaryDark,

    scrim = Color.Black.copy(alpha = 0.6f)
)

private val LightColorScheme = lightColorScheme(
    primary = InvestiaPrimary,
    onPrimary = Color.White,
    primaryContainer = InvestiaPrimaryLight.copy(alpha = 0.15f),
    onPrimaryContainer = InvestiaPrimaryDark,

    secondary = InvestiaSecondary,
    onSecondary = Color.White,

    tertiary = InvestiaAccent,
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
fun InvestiaTheme(
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
        typography = InvestiaTypography,
        content = content
    )
}
