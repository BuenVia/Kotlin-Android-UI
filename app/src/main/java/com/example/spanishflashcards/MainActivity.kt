package com.example.spanishflashcards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.spanishflashcards.data.model.language.CurrentLanguage
import com.example.spanishflashcards.data.model.language.Language
import com.example.spanishflashcards.ui.navigation.AppNavHost
import com.example.spanishflashcards.ui.theme.CustomColors
import com.example.spanishflashcards.ui.theme.SpanishFlashcardsTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // If Android closed the app in the background, the user is returned to the screen they were on.
        // Restore their language before any screen loads data, otherwise it would reset to the default.
        savedInstanceState?.getString(LANGUAGE_KEY)?.let { saved ->
            CurrentLanguage.language = Language.valueOf(saved)
        }

        // Whether the Edit mode switch is offered at all. On for debug builds only, so
        // learners using a release build never see the admin controls.
        val adminAvailable = BuildConfig.DEBUG

        setContent {
            SpanishFlashcardsTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = CustomColors.backgroundColor
                ) { innerPadding ->

                    AppNavHost(
                        adminAvailable = adminAvailable,
                        modifier = Modifier.padding(innerPadding)
                    )

                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LANGUAGE_KEY, CurrentLanguage.language.name)
    }

    companion object {
        private const val LANGUAGE_KEY = "current_language"
    }
}
