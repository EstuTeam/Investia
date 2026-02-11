package com.midastrading.app.presentation.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography

// Using default sans-serif which maps to system font (Roboto on Android, close to Inter)
val MidasFontFamily = FontFamily.Default

val MidasTypography = Typography(
    // Display - Hero numbers/titles
    displayLarge = TextStyle(
        fontFamily = MidasFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 36.sp,
        lineHeight = 40.sp,
        letterSpacing = (-1).sp
    ),
    displayMedium = TextStyle(
        fontFamily = MidasFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 30.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.5).sp
    ),
    displaySmall = TextStyle(
        fontFamily = MidasFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.25).sp
    ),

    // Headlines - Section titles
    headlineLarge = TextStyle(
        fontFamily = MidasFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = MidasFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = MidasFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),

    // Title - Card titles, list items
    titleLarge = TextStyle(
        fontFamily = MidasFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = MidasFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    titleSmall = TextStyle(
        fontFamily = MidasFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),

    // Body text
    bodyLarge = TextStyle(
        fontFamily = MidasFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = MidasFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    bodySmall = TextStyle(
        fontFamily = MidasFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp
    ),

    // Labels - Chips, badges, buttons
    labelLarge = TextStyle(
        fontFamily = MidasFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    labelMedium = TextStyle(
        fontFamily = MidasFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = MidasFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp
    )
)
