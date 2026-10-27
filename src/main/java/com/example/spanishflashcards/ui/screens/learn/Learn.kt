
package com.example.spanishflashcards.ui.screens.learn

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.data.remote.RetrofitInstance
import com.example.spanishflashcards.data.model.subject.Subject
import com.example.spanishflashcards.data.model.vocab.Vocab
import com.example.spanishflashcards.ui.utils.AppScreen
import com.example.spanishflashcards.ui.utils.ScreenTitle
import com.example.spanishflashcards.ui.theme.CustomColors
import com.example.spanishflashcards.ui.theme.ButtonShape
import com.example.spanishflashcards.ui.theme.primaryButtonColors
import com.example.spanishflashcards.ui.theme.secondaryButtonColors

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

    AppScreen(onBack = onBack) {

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
                    color = CustomColors.textColor
                )
            } else if (indexNum < lenVocabs) {

                // Subject heading
                ScreenTitle(text = subject?.subjectName ?: "")

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
                        containerColor = CustomColors.surfaceColor
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
                            color = CustomColors.textColor
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
                    color = CustomColors.secondaryTextColor
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
                        shape = ButtonShape,
                        colors = primaryButtonColors()
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
                        shape = ButtonShape,
                        colors = secondaryButtonColors()
                    ) {
                        Text("Bad", fontSize = 16.sp)
                    }
                }

            } else {

                // End of session
                ScreenTitle(
                    text = if (failedVocab.isEmpty()) {
                        "All finished!"
                    } else {
                        "Review your difficult words"
                    }
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
                                containerColor = CustomColors.surfaceColor
                            ),
                            border = BorderStroke(
                                1.dp,
                                CustomColors.accentColor
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
                                    color = CustomColors.textColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Tap a word to switch between English and Spanish",
                        fontSize = 14.sp,
                        color = CustomColors.secondaryTextColor
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
                    shape = ButtonShape,
                    colors = primaryButtonColors()
                ) {
                    Text("Back to Subject", fontSize = 16.sp)
                }
            }
        }
    }
}
