package com.example.spanishflashcards.learn

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun LearnFunction(onBack: () -> Unit) {

    var  answerVisible by remember {
        mutableStateOf(false)
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        Column(
            modifier = Modifier.padding(32.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Text
            if (answerVisible) {
                Text("Dog", fontSize = 48.sp)
            } else {
                Text("Perro", fontSize = 48.sp)
            }

            // Space
            Spacer(modifier = Modifier.height(24.dp))

            // Button
            Button(onClick = {
                answerVisible = !answerVisible
            }) {
                Text(
                    if (answerVisible) {
                        "Hide English"
                    } else {
                        "Show answer"
                    }
                )
            }

            // Space
            Spacer(modifier = Modifier.height(24.dp))

            // Button
            Button(onClick = {onBack()}) {
                Text("Back")
            }

        }

    }
}

