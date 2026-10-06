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
import com.example.spanishflashcards.model.Subject
import androidx.compose.runtime.LaunchedEffect
import com.example.spanishflashcards.api.RetrofitInstance

@Composable
fun LearnFunction(subjectId: Long, onBack: () -> Unit) {

    var subject by remember {
        mutableStateOf<Subject?>(null)
    }
    LaunchedEffect(Unit) {
        try {
            subject = RetrofitInstance.api.getSubject(subjectId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    var  answerVisible by remember {
        mutableStateOf(false)
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        Column(
            modifier = Modifier.padding(32.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(text = subject?.subjectName?: "Loading...",
                fontSize = 64.sp)

            // Text
            if (answerVisible) {
                Text(text = subject?.vocabs?.get(0)?.esp?: "Loading...",
                    fontSize = 48.sp)
            } else {
                Text(text = subject?.vocabs?.get(0)?.eng?: "Loading...",
                    fontSize = 48.sp)
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

