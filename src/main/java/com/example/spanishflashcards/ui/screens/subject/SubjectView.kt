package com.example.spanishflashcards.ui.screens.subject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.data.model.subject.Subject
import com.example.spanishflashcards.data.remote.RetrofitInstance
import com.example.spanishflashcards.ui.theme.CustomColors
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.example.spanishflashcards.ui.theme.ButtonShape
import com.example.spanishflashcards.ui.theme.primaryButtonColors
import com.example.spanishflashcards.ui.theme.secondaryButtonColors
import com.example.spanishflashcards.ui.theme.surfaceButtonColors
import com.example.spanishflashcards.ui.utils.AppScreen
import com.example.spanishflashcards.ui.utils.ScreenTitle

@Composable
fun SubjectViewFunction(
    subjectId: Long,
    isAdmin: Boolean,
    onBack: () -> Unit,
    onLearn: () -> Unit,
    onExam: () -> Unit,
    onCreateVocab: () -> Unit,
    onEditVocab: (vocabId: Long) -> Unit,
    onCreateLesson: () -> Unit,
    onEditLesson: (lessonId: Long) -> Unit,
    onEditSubject: () -> Unit
) {

    var subject by remember {
        mutableStateOf<Subject?>(null)
    }

    LaunchedEffect(subjectId) {
        try {
            subject = RetrofitInstance.api.getSubject(subjectId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val subjectLesson = subject?.lesson
    val vocabList = subject?.vocabs

    AppScreen(onBack = onBack) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Subject heading
            ScreenTitle(text = subject?.subjectName ?: "Loading...")

            Spacer(modifier = Modifier.height(16.dp))

            // Lesson
            if (subjectLesson != null) {

                Text(
                    text = if (subjectLesson.lessonTitle.isNullOrBlank()) {"EMPTY"} else {
                        subjectLesson.lessonTitle
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Markdown(
                    modifier = Modifier.fillMaxWidth(),
                    content = subjectLesson.body.toString(),
                    colors = markdownColor(
                        text = CustomColors.textColor
                    )
                )

                if (isAdmin) {

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { onEditLesson(subjectLesson.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = ButtonShape,
                        colors = surfaceButtonColors()
                    ) {
                        Text(
                            text = "Edit a Lesson",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

            } else if (isAdmin) {
                Button(
                    onClick = { onCreateLesson() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = ButtonShape,
                    colors = surfaceButtonColors()
                ) {
                    Text(
                        text = "Create a Lesson",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Choose how you want to practise",
                fontSize = 14.sp,
                color = CustomColors.secondaryTextColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Learn and Exam buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onLearn() },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = primaryButtonColors()
                ) {
                    Text(
                        text = "Learn",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = { onExam() },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = secondaryButtonColors()
                ) {
                    Text(
                        text = "Exam",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isAdmin) {

                // Create vocabulary
                Button(
                    onClick = { onCreateVocab() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = ButtonShape,
                    colors = surfaceButtonColors()
                ) {
                    Text(
                        text = "+  Create vocabulary",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
            }

            // Vocabulary section heading
            Text(
                text = "Vocabulary",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = CustomColors.textColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Vocabulary items - no nested LazyColumn
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                vocabList.orEmpty().forEach { vocab ->

                    if (isAdmin) {
                        // Admins can tap a word to edit or delete it
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            shape = ButtonShape,
                            colors = surfaceButtonColors(),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 1.dp
                            ),
                            onClick = { onEditVocab(vocab.id) }
                        ) {
                            VocabText(vocab.esp)
                        }
                    } else {
                        VocabText(vocab.esp)
                    }
                }
            }

            if (isAdmin) {

                Spacer(modifier = Modifier.height(20.dp))

                // Edit Subject
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = ButtonShape,
                    colors = secondaryButtonColors(),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 1.dp
                    ),
                    onClick = { onEditSubject() }
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
                        color = CustomColors.textColor
                    )
                }
            }
        }
    }
}

@Composable
private fun VocabText(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 18.dp,
                vertical = 16.dp
            ),
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        color = CustomColors.textColor
    )
}
