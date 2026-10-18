package com.example.spanishflashcards.subject

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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.Screen
import com.example.spanishflashcards.api.RetrofitInstance
import com.example.spanishflashcards.create.CreateVocabulary
import com.example.spanishflashcards.exam.ExamFunction
import com.example.spanishflashcards.learn.LearnFunction
import com.example.spanishflashcards.model.Subject
import com.example.spanishflashcards.model.Vocab
import com.example.spanishflashcards.vocab.ViewVocabFunction
import com.example.spanishflashcards.subject.ViewSubjectEditDelete

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign


enum class LearnScreen {
    LEARN,
    EXAM,
    CREATE_VOCAB,
    VIEW_VOCAB,
    VIEW_SUBJECT
}

@Composable
fun SubjectViewFunction(subjectId: Long, onBack: () -> Unit) {

    var learnChoice by remember {
        mutableStateOf<LearnScreen?>(null)
    }

    var subjectChoice by remember {
        mutableLongStateOf(0)
    }

    var subject by remember {
        mutableStateOf<Subject?>(null)
    }

    var vocabList: List<Vocab>? by remember {
        mutableStateOf(listOf< Vocab>())
    }

    var vocabId by remember {
        mutableLongStateOf(0)
    }

    var refreshTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit, refreshTrigger) {
        try {
            subject = RetrofitInstance.api.getSubject(subjectId)
            vocabList = subject?.vocabs
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    when (learnChoice) {
        LearnScreen.LEARN -> LearnFunction(
            subjectId,
            onBack = { learnChoice = null }
        )

        LearnScreen.EXAM -> ExamFunction(
            subjectId,
            onBack = { learnChoice = null }
        )

        LearnScreen.CREATE_VOCAB -> CreateVocabulary(
            subjectId,
            onBack = {
                learnChoice = null
                refreshTrigger++
            }
        )

        LearnScreen.VIEW_VOCAB -> ViewVocabFunction(
            vocabId,
            subjectId,
            onBack = {
                learnChoice = null
                refreshTrigger++
            })

        LearnScreen.VIEW_SUBJECT -> ViewSubjectEditDelete(
            subjectId,
            onBack = {
                learnChoice = null
                refreshTrigger++
            }
        )

        null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FC))
                    .padding(24.dp)
            )
            {

                // Back button
                Text(
                    text = "← Back",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clickable {
                            vocabList = listOf<Vocab>()
                            onBack()
                        }
                        .padding(12.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF475569)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 56.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Subject heading
                    Text(
                        text = subject?.subjectName ?: "Loading...",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF172033),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Choose how you want to practise",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Main actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Button(
                            onClick = {
                                subjectChoice = subjectId
                                learnChoice = LearnScreen.LEARN
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4F46E5),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Learn",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = {
                                subjectChoice = subjectId
                                learnChoice = LearnScreen.EXAM
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE0E7FF),
                                contentColor = Color(0xFF3730A3)
                            )
                        ) {
                            Text(
                                text = "Exam",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Secondary action
                    Button(
                        onClick = {
                            subjectChoice = subjectId
                            learnChoice = LearnScreen.CREATE_VOCAB
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF475569)
                        )
                    ) {
                        Text(
                            text = "+  Create vocabulary",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Vocabulary section heading
                    Text(
                        text = "Vocabulary",
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF172033)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Scrollable vocabulary list
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(vocabList.orEmpty()) { vocab ->

                            Button (
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White
                                ),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 1.dp
                                ),
                                onClick = {
                                    vocabId = vocab.id
                                    learnChoice = LearnScreen.VIEW_VOCAB
                                }
                            ) {
                                Text(
                                    text = vocab.esp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            horizontal = 18.dp,
                                            vertical = 16.dp
                                        ),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF334155)
                                )
                            }
                        }
                    }

                    // Edit Subject
                    Button (
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE0E7FF),
                            contentColor = Color(0xFF3730A3)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 1.dp
                        ),
                        onClick = {
                            learnChoice = LearnScreen.VIEW_SUBJECT
                        }
                    ) {
                        Text(
                            text = "Edit Subject",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 18.dp,
                                    vertical = 16.dp
                                ),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF334155)
                        )
                    }
                }
            }

        }

    }

}