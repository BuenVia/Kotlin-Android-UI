package com.example.spanishflashcards.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

// Shared component styles so every screen looks the same.
// Colours come from CustomColors in Color.kt - no HEX codes belong in this file.

val ButtonShape = RoundedCornerShape(14.dp)
val CardShape = RoundedCornerShape(18.dp)

// Main call-to-action, e.g. "Create", "Save", "Check answer"
@Composable
fun primaryButtonColors(): ButtonColors = ButtonDefaults.buttonColors(
    containerColor = CustomColors.primaryColor,
    contentColor = CustomColors.onPrimaryColor,
    disabledContainerColor = CustomColors.primaryDisabledColor,
    disabledContentColor = CustomColors.onPrimaryColor
)

// Lighter alternative shown alongside a primary button, e.g. "Exam", "Bad", "Back"
@Composable
fun secondaryButtonColors(): ButtonColors = ButtonDefaults.buttonColors(
    containerColor = CustomColors.accentColor,
    contentColor = CustomColors.onAccentColor,
    disabledContainerColor = CustomColors.disabledContainerColor,
    disabledContentColor = CustomColors.disabledContentColor
)

// White button that blends in with cards, e.g. list items and admin actions
@Composable
fun surfaceButtonColors(): ButtonColors = ButtonDefaults.buttonColors(
    containerColor = CustomColors.surfaceColor,
    contentColor = CustomColors.textColor,
    disabledContainerColor = CustomColors.disabledContainerColor,
    disabledContentColor = CustomColors.disabledContentColor
)

@Composable
fun appTextFieldColors(): TextFieldColors = TextFieldDefaults.colors(
    focusedContainerColor = CustomColors.surfaceColor,
    unfocusedContainerColor = CustomColors.surfaceColor,
    disabledContainerColor = CustomColors.surfaceColor,
    focusedIndicatorColor = CustomColors.transparent,
    unfocusedIndicatorColor = CustomColors.transparent,
    disabledIndicatorColor = CustomColors.transparent,
    cursorColor = CustomColors.primaryColor,
    focusedLabelColor = CustomColors.primaryColor,
    unfocusedLabelColor = CustomColors.secondaryTextColor,
    focusedTextColor = CustomColors.textColor,
    unfocusedTextColor = CustomColors.textColor,
    disabledTextColor = CustomColors.secondaryTextColor
)
