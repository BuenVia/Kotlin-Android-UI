package com.example.spanishflashcards.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Material components fall back to these when no colours are passed explicitly,
// so they are mapped onto the app palette in Color.kt.
// The same scheme is used in light and dark mode so the app always looks the same.
private val AppColorScheme = lightColorScheme(
    primary = CustomColors.primaryColor,
    onPrimary = CustomColors.onPrimaryColor,
    primaryContainer = CustomColors.accentColor,
    onPrimaryContainer = CustomColors.onAccentColor,
    secondary = CustomColors.primaryColor,
    onSecondary = CustomColors.onPrimaryColor,
    secondaryContainer = CustomColors.accentColor,
    onSecondaryContainer = CustomColors.onAccentColor,
    background = CustomColors.backgroundColor,
    onBackground = CustomColors.textColor,
    surface = CustomColors.surfaceColor,
    onSurface = CustomColors.textColor,
    onSurfaceVariant = CustomColors.secondaryTextColor,
    error = CustomColors.errorColor
)

@Composable
fun SpanishFlashcardsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}
