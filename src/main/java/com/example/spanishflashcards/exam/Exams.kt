package com.example.spanishflashcards.exam

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExamFunction(onBack: () -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var userAnswer by remember {
        mutableStateOf("")
    }

    var correctAnswer = "Dog"

    var result by remember {
        mutableStateOf("")
    }

    fun checkAnswer() {
        result = if (
            userAnswer.equals(correctAnswer, ignoreCase = true)
        ) {
            "Correct"
        } else {
            "Incorrect"
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        Column(
            modifier = Modifier.padding(32.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Text
            Text("Perro", fontSize = 48.sp)

            // Space
            Spacer(modifier = Modifier.height(24.dp))

            // Input
            TextField(
                value = userAnswer,
                onValueChange = { userAnswer = it },
                label = {Text("Your Answer")},
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        checkAnswer()
                        keyboardController?.hide()
                    }
                )
            )

            // Space
            Spacer(modifier = Modifier.height(24.dp))

            // Button
            Button(onClick = { checkAnswer() }) {
                Text("Check answer")
            }

            // Space
            Spacer(modifier = Modifier.height(24.dp))

            // Result
            Text(text = result, fontSize = 20.sp)

            // Space
            Spacer(modifier = Modifier.height(24.dp))

            // Button
            Button(onClick = {onBack()}) {
                Text("Back")
            }
        }

    }
}