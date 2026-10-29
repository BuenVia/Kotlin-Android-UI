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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.data.model.lesson.EditLesson
import com.example.spanishflashcards.data.model.language.CurrentLanguage
import com.example.spanishflashcards.data.remote.RetrofitInstance
import kotlinx.coroutines.launch
import com.example.spanishflashcards.ui.utils.AppScreen
import com.example.spanishflashcards.ui.utils.LESSON_MARKDOWN_HINT
import com.example.spanishflashcards.ui.utils.LoadError
import com.example.spanishflashcards.ui.utils.ScreenTitle
import com.example.spanishflashcards.ui.theme.ButtonShape
import com.example.spanishflashcards.ui.theme.CustomColors
import com.example.spanishflashcards.ui.theme.primaryButtonColors
import com.example.spanishflashcards.ui.theme.appTextFieldColors


@Composable
fun EditLessonView(lessonId: Long, subjectId: Long, onBack: () -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()


    // As loaded from the API - reloaded after a rotation
    var loadedLesson by remember {
        mutableStateOf<EditLesson?>(null)
    }

    // The user's edits. Saveable so they survive rotating the screen; null until loaded.
    var lessonTitle by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var body by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val lesson = loadedLesson?.let {
        it.copy(lessonTitle = lessonTitle ?: it.lessonTitle, body = body ?: it.body)
    }

    var loadFailed by remember {
        mutableStateOf(false)
    }

    // Bumped by the Retry button to run the load again
    var loadAttempt by remember {
        mutableIntStateOf(0)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    LaunchedEffect(lessonId, loadAttempt) {
        loadFailed = false
        try {
            loadedLesson = RetrofitInstance.api.getLesson(CurrentLanguage.pathSegment, lessonId)
        } catch (e: Exception) {
            e.printStackTrace()
            loadFailed = true
        }
    }

    // Fields are only usable once the lesson has loaded and while nothing is saving
    val canEdit = !isLoading && lesson != null

            AppScreen(onBack = onBack, backEnabled = !isLoading) {

                if (loadFailed && lesson == null) {
                    LoadError(
                        message = "Unable to load this lesson. Please try again.",
                        onRetry = { loadAttempt++ },
                        modifier = Modifier.align(Alignment.Center)
                    )
                    return@AppScreen
                }

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
                                lessonTitle = newVal
                            },
                            label = {
                                Text("Title")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = canEdit,
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
                                body = newVal
                            },
                            label = {
                                Text("Body")
                            },
                            // Reminder of the table syntax, the one Markdown feature that is easy to get wrong
                            supportingText = {
                                Text(LESSON_MARKDOWN_HINT)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 10,
                            maxLines = 20,
                            enabled = canEdit,
                            shape = ButtonShape,
                            colors = appTextFieldColors(),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Default
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Error feedback
                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                fontSize = 14.sp,
                                color = CustomColors.errorColor
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                        }

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
                                enabled = canEdit,
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
        RetrofitInstance.api.updateLesson(CurrentLanguage.pathSegment, lessonId, lessonVal)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}