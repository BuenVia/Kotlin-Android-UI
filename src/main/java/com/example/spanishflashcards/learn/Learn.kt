package com.example.spanishflashcards.learn

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.model.Subject
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

    var indexNum by remember {
        mutableIntStateOf(0)
    }

    var answerVisible by remember {
        mutableStateOf(false)
    }

    val lenVocabs = subject?.vocabs?.size ?: 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        // Back button
        Button(
            onClick = { onBack() },
            modifier = Modifier.align(Alignment.TopEnd).padding(25.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
        ) {
            Text("Back")
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = subject?.subjectName ?: "Loading...",
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = if (answerVisible) {
                            subject?.vocabs?.get(indexNum)?.esp ?: "Loading..."
                        } else {
                            subject?.vocabs?.get(indexNum)?.eng ?: "Loading..."
                        },
                        fontSize = 36.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main action
            Button(
                onClick = {
                    answerVisible = !answerVisible
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3F51B5),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (answerVisible) {
                        "Hide answer"
                    } else {
                        "Show answer"
                    },
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Next / Finish
            if (indexNum < lenVocabs - 1) {

                Button(
                    onClick = {
                        indexNum += 1
                        answerVisible = false
                    },
                    modifier = Modifier
                        .width(104.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0FFF50),
                        contentColor = Color(0xFF097969)
                    )
                ) {
                    Text(
                        text = "Next",
                        fontSize = 16.sp
                    )
                }

            } else {

                Button(
                    onClick = {
                        onBack()
                    },
                    modifier = Modifier
                        .width(104.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE8EAF6),
                        contentColor = Color(0xFF3F51B5)
                    )
                ) {
                    Text(
                        text = "Finish",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
