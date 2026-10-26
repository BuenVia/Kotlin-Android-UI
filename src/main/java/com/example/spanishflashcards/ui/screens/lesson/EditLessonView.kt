package com.example.spanishflashcards.ui.screens.lesson

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.spanishflashcards.data.model.EditLesson.EditLesson
import com.example.spanishflashcards.data.model.EditLesson.Lesson
import com.example.spanishflashcards.data.remote.RetrofitInstance
import com.example.spanishflashcards.ui.screens.subject.SubjectAdminViewFunction
import com.example.spanishflashcards.ui.screens.subject.SubjectViewFunction
import com.example.spanishflashcards.ui.screens.vocab.EditDeleteScreen
import com.example.spanishflashcards.ui.screens.vocab.handleVocabEdit
import kotlinx.coroutines.launch


@Composable
fun EditLessonView(lessonId: Long, subjectId: Long, onBack: () -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    val primaryColor = Color(0xFF4F46E5)
    val backgroundColor = Color(0xFFF8F9FC)
    val textColor = Color(0xFF1F2937)
    val secondaryTextColor = Color(0xFF6B7280)

    var lesson by remember {
        mutableStateOf<EditLesson?>(null)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    LaunchedEffect(lessonId) {
        try {
            lesson = RetrofitInstance.api.getLesson(lessonId);
            println(lesson)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                // Back button
                Button(
                    onClick = { onBack() },
                    enabled = !isLoading,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE0E7FF),
                        contentColor = primaryColor
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

                    Text(text = "Edit or Delete Lesson - " + lessonId)


                    Column(
                        modifier = Modifier.padding(10.dp),
//                horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextField(
                            value = lesson?.lessonTitle ?: "",
                            onValueChange = { newVal ->
                                lesson = lesson?.copy(lessonTitle = newVal)
                            },
                            label = {
                                Text("Title")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !isLoading,
                            shape = RoundedCornerShape(14.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = primaryColor,
                                focusedLabelColor = primaryColor,
                                unfocusedLabelColor = secondaryTextColor,
                                focusedTextColor = textColor
                            ),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    // Move focus to the next field using the keyboard.
                                }
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        TextField(
                            value = lesson?.body ?: "",
                            onValueChange = { newVal ->
                                lesson = lesson?.copy(body = newVal)
                            },
                            label = {
                                Text("Body")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 10,
                            maxLines = 20,
                            enabled = !isLoading,
                            shape = RoundedCornerShape(14.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = primaryColor,
                                focusedLabelColor = primaryColor,
                                unfocusedLabelColor = secondaryTextColor
                            ),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Default
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    keyboardController?.hide()

                                    if (lesson?.lessonTitle?.isBlank() == true || lesson?.body?.isBlank() == true) {
                                        errorMessage = "Please complete both fields."
                                    } else {
                                        coroutineScope.launch {
                                            isLoading = true
                                            errorMessage = ""

                                            val result = handleLessonEdit(lessonId, lesson)

                                            isLoading = false

                                            if (result) {
                                                onBack()
                                            } else {
                                                errorMessage = "Unable to edit lesson."
                                            }
                                        }
                                    }
                                },
                                enabled = !isLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryColor,
                                    contentColor = Color.White,
                                    disabledContainerColor = Color(0xFFC7C9F8),
                                    disabledContentColor = Color.White
                                )
                            ) {
                                Text("Edit")
                            }

                        }
                    }

                }
            }
        }


suspend fun handleLessonEdit(lessonId: Long, lessonVal: EditLesson?): Boolean {
    return try {
        var editLessonRes: Lesson
        editLessonRes = RetrofitInstance.api.updateLesson(lessonId, lessonVal)
        println(editLessonRes)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}