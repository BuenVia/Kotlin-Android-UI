package com.example.spanishflashcards.ui.screens.subject

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.spanishflashcards.data.remote.RetrofitInstance
import com.example.spanishflashcards.ui.screens.vocab.CreateVocabulary
import com.example.spanishflashcards.ui.screens.exam.ExamFunction
import com.example.spanishflashcards.ui.screens.learn.LearnFunction
import com.example.spanishflashcards.data.model.subject.Subject
import com.example.spanishflashcards.data.model.vocab.Vocab
import com.example.spanishflashcards.ui.screens.vocab.ViewVocabFunction
import com.mikepenz.markdown.m3.Markdown


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.spanishflashcards.data.model.note.Note
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.model.MarkdownColors


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

    var subjectNote by remember {
        mutableStateOf<Note?>(null)
    }

    var vocabList: List<Vocab>? by remember {
        mutableStateOf(listOf< Vocab>())
    }

    var vocabId by remember {
        mutableLongStateOf(0)
    }

    var refreshTrigger by remember { mutableIntStateOf(0) }

//    val article = """
//        # Learning Spanish
//
//        Learning a new language takes time,
//        but consistency is key.
//
//        ## Three tips for beginners
//
//        - Practise every day.
//        - Listen to Spanish podcasts.
//        - Learn vocabulary in context.
//
//        **Tip:** Try to speak Spanish whenever possible.
//
//        *Small steps make a big difference.*
//    """.trimIndent()


    LaunchedEffect(Unit, refreshTrigger) {
        try {
            subject = RetrofitInstance.api.getSubject(subjectId)
            subjectNote = subject?.note
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
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Back button
                    Text(
                        text = "← Back",
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable {
                                vocabList = listOf<Vocab>()
                                onBack()
                            }
                            .padding(12.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569)
                    )

                    // Subject heading
                    Text(
                        text = subject?.subjectName ?: "Loading...",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF172033),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Markdown article - takes its natural height
                    Markdown(
                        modifier = Modifier.fillMaxWidth(),
                        content = subjectNote?.body.toString(),
                        colors = markdownColor(
                            text = Color(0xFF212121)
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Choose how you want to practise",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Learn and Exam buttons
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

                    // Vocabulary section heading
                    Text(
                        text = "Vocabulary",
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF172033)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Vocabulary items - no nested LazyColumn
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        vocabList.orEmpty().forEach { vocab ->


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
            }

        }

    }

}