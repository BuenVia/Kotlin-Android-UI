
package com.example.spanishflashcards.ui.screens.subject

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.data.model.language.CurrentLanguage
import com.example.spanishflashcards.data.remote.RetrofitInstance
import com.example.spanishflashcards.data.model.subject.CreateSubject
import kotlinx.coroutines.launch
import com.example.spanishflashcards.ui.utils.AppScreen
import com.example.spanishflashcards.ui.utils.ScreenTitle
import com.example.spanishflashcards.ui.theme.CustomColors
import com.example.spanishflashcards.ui.theme.ButtonShape
import com.example.spanishflashcards.ui.theme.primaryButtonColors
import com.example.spanishflashcards.ui.theme.appTextFieldColors

@Composable
fun CreateFunction(onBack: () -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    // Saveable so typed text survives rotating the screen
    var subjectName by rememberSaveable {
        mutableStateOf("")
    }

    val subjectVal = CreateSubject(subjectName = subjectName)

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

            // Heading
            ScreenTitle(text = "Create a Subject")

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Give your vocabulary collection a name.",
                fontSize = 14.sp,
                color = CustomColors.secondaryTextColor
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Subject name input
            TextField(
                value = subjectVal.subjectName,
                onValueChange = {
                    subjectName = it
                    errorMessage = ""
                },
                label = {
                    Text("Subject name")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading,
                shape = ButtonShape,
                colors = appTextFieldColors(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Error message
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

                    if (subjectVal.subjectName.isBlank()) {
                        errorMessage = "Please enter a subject name."
                    } else {
                        coroutineScope.launch {
                            isLoading = true
                            errorMessage = ""

                            val result = handleSubjectSubmit(subjectVal)

                            isLoading = false

                            if (result) {
                                onBack()
                            } else {
                                errorMessage =
                                    "Unable to create the subject. Please try again."
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
                        text = "Create Subject",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

suspend fun handleSubjectSubmit(
    subjectVal: CreateSubject
): Boolean {

    return try {
        if (subjectVal.subjectName.isBlank()) {
            false
        } else {
            RetrofitInstance.api.createSubject(CurrentLanguage.pathSegment, subjectVal)
            true
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

@Composable
fun editSubject(subjectId: Long, onBack: () -> Unit) {
    // TODO write logic for editing a subject
}

@Composable
fun deleteSubject(subjectId: Long, onBack: () -> Unit) {
    // TODO write logic for deleting a subject
}