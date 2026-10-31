package com.example.spanishflashcards.ui.screens.language

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.data.model.language.Language
import com.example.spanishflashcards.ui.theme.CardShape
import com.example.spanishflashcards.ui.theme.CustomColors
import com.example.spanishflashcards.ui.theme.surfaceButtonColors
import com.example.spanishflashcards.ui.utils.AppScreen
import com.example.spanishflashcards.ui.utils.ScreenTitle

// First screen shown when the app opens. Also reached from Home to change language,
// in which case onBack is given so the user can return without changing anything.
@Composable
fun LanguageSelectScreen(
    onLanguageSelected: (Language) -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {

    AppScreen(modifier = modifier, onBack = onBack) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            ScreenTitle("Choose a language")

            Text(
                text = "Which language would you like to learn?",
                fontSize = 15.sp,
                color = CustomColors.secondaryTextColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // One button per language the API supports
            Language.entries.forEach { language ->
                Button(
                    onClick = { onLanguageSelected(language) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = CardShape,
                    colors = surfaceButtonColors(),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Text(
                        text = language.displayName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
