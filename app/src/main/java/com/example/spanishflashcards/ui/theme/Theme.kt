package com.example.spanishflashcards.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Material components fall back to these when no colours are passed explicitly,
// so they are mapped onto the app palette in Color.kt.
// The app is always dark, whatever the phone's light/dark setting.
private val AppColorScheme = darkColorScheme(
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
    // No tint - otherwise raised cards are shaded with the primary colour
    surfaceTint = CustomColors.surfaceColor,
    onSurface = CustomColors.textColor,
    onSurfaceVariant = CustomColors.secondaryTextColor,
    outline = CustomColors.borderColor,
    outlineVariant = CustomColors.borderColor,
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
