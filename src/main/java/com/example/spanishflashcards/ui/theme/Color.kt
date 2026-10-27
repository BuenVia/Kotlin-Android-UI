package com.example.spanishflashcards.ui.theme

import androidx.compose.ui.graphics.Color

// The app's single colour palette.
// Every colour used in the UI comes from here - to re-theme the app, only change the HEX codes below.
object CustomColors {

    // Brand
    val primaryColor = Color(0xFF4F46E5)
    val primaryDisabledColor = Color(0xFFC7C9F8)
    val onPrimaryColor = Color(0xFFFFFFFF)

    // Light accent used for secondary buttons, the back button and borders
    val accentColor = Color(0xFFE0E7FF)
    val onAccentColor = Color(0xFF3730A3)

    // Surfaces
    val backgroundColor = Color(0xFFF8F9FC)
    val surfaceColor = Color(0xFFFFFFFF)

    // Text
    val textColor = Color(0xFF1F2937)
    val secondaryTextColor = Color(0xFF6B7280)

    // Disabled state for non-primary buttons
    val disabledContainerColor = Color(0xFFE5E7EB)
    val disabledContentColor = Color(0xFF9CA3AF)

    // Feedback
    val successColor = Color(0xFF15803D)
    val errorColor = Color(0xFFB91C1C)

    // Kept for convenience where a component needs "no colour"
    val transparent = Color.Transparent
}
