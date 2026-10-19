
package com.example.spanishflashcards.ui.screens.learn

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.data.remote.RetrofitInstance
import com.example.spanishflashcards.data.model.subject.Subject
import com.example.spanishflashcards.data.model.vocab.Vocab

@Composable
fun LearnFunction(subjectId: Long, onBack: () -> Unit) {

    var subject by remember {
        mutableStateOf<Subject?>(null)
    }

    var vocabList by remember {
        mutableStateOf<List<Vocab>>(emptyList())
    }

    var indexNum by remember {
        mutableIntStateOf(0)
    }

    var answerVisible by remember {
        mutableStateOf(false)
    }

    var failedVocab by remember {
        mutableStateOf<List<Vocab>>(emptyList())
    }

    val lenVocabs = subject?.vocabs?.size ?: 0

    LaunchedEffect(subjectId) {
        try {
            subject = RetrofitInstance.api.getSubject(subjectId)
            vocabList = subject?.vocabs ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FC))
            .padding(24.dp)
    ) {

        // Back button
        Button(
            onClick = { onBack() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE0E7FF),
                contentColor = Color(0xFF4F46E5)
            )
        ) {
            Text("Back")
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (subject == null) {
                Text(
                    text = "Loading...",
                    fontSize = 18.sp,
                    color = Color(0xFF374151)
                )
            } else if (indexNum < lenVocabs) {

                // Subject heading
                Text(
                    text = subject?.subjectName ?: "",
                    fontSize = 26.sp,
                    color = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Flashcard
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clickable {
                            answerVisible = !answerVisible
                        },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
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
                                subject?.vocabs?.get(indexNum)?.esp ?: ""
                            } else {
                                subject?.vocabs?.get(indexNum)?.eng ?: ""
                            },
                            fontSize = 32.sp,
                            color = Color(0xFF1F2937)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (answerVisible) {
                        "Tap the card to hide the answer"
                    } else {
                        "Tap the card to reveal the answer"
                    },
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Good and Bad buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Button(
                        onClick = {
                            if (answerVisible) {
                                indexNum++
                                answerVisible = false
                            }
                        },
                        enabled = answerVisible,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4F46E5),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFE5E7EB),
                            disabledContentColor = Color(0xFF9CA3AF)
                        )
                    ) {
                        Text("Good", fontSize = 16.sp)
                    }

                    Button(
                        onClick = {
                            if (answerVisible) {
                                vocabList.getOrNull(indexNum)?.let {
                                    failedVocab = failedVocab + it
                                }
                                indexNum++
                                answerVisible = false
                            }
                        },
                        enabled = answerVisible,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE0E7FF),
                            contentColor = Color(0xFF4F46E5),
                            disabledContainerColor = Color(0xFFF3F4F6),
                            disabledContentColor = Color(0xFF9CA3AF)
                        )
                    ) {
                        Text("Bad", fontSize = 16.sp)
                    }
                }

            } else {

                // End of session
                Text(
                    text = if (failedVocab.isEmpty()) {
                        "All finished!"
                    } else {
                        "Review your difficult words"
                    },
                    fontSize = 24.sp,
                    color = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (failedVocab.isNotEmpty()) {

                    failedVocab.forEach { vocab ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable {
                                    answerVisible = !answerVisible
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            border = BorderStroke(
                                1.dp,
                                Color(0xFFE0E7FF)
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (answerVisible) {
                                        vocab.esp
                                    } else {
                                        vocab.eng
                                    },
                                    fontSize = 16.sp,
                                    color = Color(0xFF374151)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Tap a word to switch between English and Spanish",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4F46E5),
                        contentColor = Color.White
                    )
                ) {
                    Text("Back to Subject", fontSize = 16.sp)
                }
            }
        }
    }
}
