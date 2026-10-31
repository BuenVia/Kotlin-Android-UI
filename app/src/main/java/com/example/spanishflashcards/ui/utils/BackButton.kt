package com.example.spanishflashcards.ui.utils

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.spanishflashcards.ui.theme.ButtonShape
import com.example.spanishflashcards.ui.theme.secondaryButtonColors

// Placed by AppScreen's top bar - screens should pass onBack to AppScreen rather than use this directly
@Composable
fun BackButton(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = { onBack() },
        enabled = enabled,
        modifier = modifier,
        shape = ButtonShape,
        colors = secondaryButtonColors()
    ) {
        Text("Back")
    }
}
