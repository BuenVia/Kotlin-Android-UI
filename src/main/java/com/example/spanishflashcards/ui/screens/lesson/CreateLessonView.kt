package com.example.spanishflashcards.ui.screens.lesson

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.data.model.lesson.CreateLesson
import com.example.spanishflashcards.data.remote.RetrofitInstance
import kotlinx.coroutines.launch
import com.example.spanishflashcards.ui.utils.AppScreen
import com.example.spanishflashcards.ui.utils.ScreenTitle
import com.example.spanishflashcards.ui.theme.CustomColors
import com.example.spanishflashcards.ui.theme.ButtonShape
import com.example.spanishflashcards.ui.theme.primaryButtonColors
import com.example.spanishflashcards.ui.theme.appTextFieldColors

@Composable
fun CreateLessonView(subjectId: Long, onBack: () -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    var lessonVal by remember(subjectId) {
        mutableStateOf(
            CreateLesson(
                lessonTitle = "",
                body = "",
                subjectId = subjectId
            )
        )
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }


    AppScreen(onBack = onBack, backEnabled = !isLoading) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ScreenTitle(text = "Create a Lesson")

                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    value = lessonVal.lessonTitle,
                    onValueChange = {
                        lessonVal = lessonVal.copy(lessonTitle = it)
                        errorMessage = ""
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
                    value = lessonVal.body,
                    onValueChange = {
                        lessonVal = lessonVal.copy(body = it)
                        errorMessage = ""
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
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            // Move focus to the next field using the keyboard.
                        }
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Error feedback
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        fontSize = 14.sp,
                        color = CustomColors.errorColor
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Create button
                Button(
                    onClick = {
                        keyboardController?.hide()

                        if (lessonVal.lessonTitle.isBlank() || lessonVal.body.isBlank()) {
                            errorMessage = "Please complete all fields."
                        } else {
                            coroutineScope.launch {
                                isLoading = true
                                errorMessage = ""

                                val result = handleLessonSubmit(lessonVal)

                                isLoading = false

                                if (result) {
                                    onBack()
                                } else {
                                    errorMessage =
                                        "Unable to create lesson. Please try again."
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
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(22.dp),
                            color = CustomColors.surfaceColor,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Create Lesson",
                            fontSize = 16.sp
                        )
                    }
                }
            }
    }
}

suspend fun handleLessonSubmit(lessonVal: CreateLesson): Boolean {
    return try {
        if(lessonVal.lessonTitle.isBlank() || lessonVal.body.isBlank()) {
            false
        } else {
            RetrofitInstance.api.createLesson(lessonVal)
            true
        }

    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}