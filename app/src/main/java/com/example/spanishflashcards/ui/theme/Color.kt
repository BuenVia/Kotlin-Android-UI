package com.example.spanishflashcards.ui.theme

import androidx.compose.ui.graphics.Color

// The app's single colour palette - a dark theme matching the web app's CSS variables
// (named in the comments). Every colour used in the UI comes from here - to re-theme
// the app, only change the HEX codes below.
object CustomColors {

    // Brand
    val primaryColor = Color(0xFF818CF8)            // --primary
    val primaryDisabledColor = Color(0xFF4B5196)    // dimmed --primary
    // Dark text on the light indigo primary - white on #818CF8 is too low contrast to read
    val onPrimaryColor = Color(0xFF111216)          // --bg

    // Soft accent used for secondary buttons, the back button and badges
    val accentColor = Color(0xFF23244A)             // --primary-soft
    val onAccentColor = Color(0xFFA5B4FC)           // --primary-hover

    // Surfaces
    val backgroundColor = Color(0xFF111216)         // --bg
    val surfaceColor = Color(0xFF1A1B21)            // --surface
    val borderColor = Color(0xFF2B2D36)             // --border

    // Text
    // Much dimmer than the web's --text (#ECECF1) and --muted (#9A9CAB), which were too
    // stark on the dark background. Secondary is dimmed too, so it stays a step below text.
    val textColor = Color(0xFF808080)
    val secondaryTextColor = Color(0xFF737585)

    // Disabled state for non-primary buttons
    val disabledContainerColor = Color(0xFF2B2D36)  // --border
    val disabledContentColor = Color(0xFF555766)    // below secondary text

    // Feedback
    val successColor = Color(0xFF4ADE80)            // --ok
    val errorColor = Color(0xFFF87171)              // --danger

    // Kept for convenience where a component needs "no colour"
    val transparent = Color.Transparent
}
