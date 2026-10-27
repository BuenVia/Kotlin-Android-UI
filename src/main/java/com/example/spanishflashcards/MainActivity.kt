package com.example.spanishflashcards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.spanishflashcards.ui.navigation.AppNavHost
import com.example.spanishflashcards.ui.theme.CustomColors
import com.example.spanishflashcards.ui.theme.SpanishFlashcardsTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Set Admin mode - for dev purposes only
        val adminMode = true

        setContent {
            SpanishFlashcardsTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = CustomColors.backgroundColor
                ) { innerPadding ->

                    AppNavHost(
                        isAdmin = adminMode,
                        modifier = Modifier.padding(innerPadding)
                    )

                }
            }
        }
    }
}
