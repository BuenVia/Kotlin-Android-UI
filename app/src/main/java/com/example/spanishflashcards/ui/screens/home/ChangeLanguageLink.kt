package com.example.spanishflashcards.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.ui.theme.ButtonShape
import com.example.spanishflashcards.ui.theme.CustomColors

// Small, low-key pill for the top bar that opens the language selection screen
@Composable
fun ChangeLanguageLink(onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = { onClick() },
        modifier = modifier.heightIn(min = 32.dp),
        shape = ButtonShape,
        border = BorderStroke(1.dp, CustomColors.borderColor),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = CustomColors.secondaryTextColor
        )
    ) {
        Text(text = "Change language", fontSize = 13.sp)
    }
}
