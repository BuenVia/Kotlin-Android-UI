package com.example.spanishflashcards.ui.screens.lesson

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.spanishflashcards.data.model.lesson.EditLesson
import com.example.spanishflashcards.data.model.lesson.Lesson
import com.example.spanishflashcards.data.remote.RetrofitInstance
import kotlinx.coroutines.launch
import com.example.spanishflashcards.ui.utils.AppScreen
import com.example.spanishflashcards.ui.utils.ScreenTitle
import com.example.spanishflashcards.ui.theme.ButtonShape
import com.example.spanishflashcards.ui.theme.primaryButtonColors
import com.example.spanishflashcards.ui.theme.appTextFieldColors


@Composable
fun EditLessonView(lessonId: Long, subjectId: Long, onBack: () -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()


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

            AppScreen(onBack = onBack, backEnabled = !isLoading) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    ScreenTitle(text = "Edit Lesson")

                    Spacer(modifier = Modifier.height(20.dp))

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
                            shape = ButtonShape,
                            colors = appTextFieldColors(),
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
                            shape = ButtonShape,
                            colors = appTextFieldColors(),
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
                                shape = ButtonShape,
                                colors = primaryButtonColors()
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