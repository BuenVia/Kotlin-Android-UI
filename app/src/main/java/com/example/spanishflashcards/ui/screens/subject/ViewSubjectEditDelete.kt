package com.example.spanishflashcards.ui.screens.subject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.spanishflashcards.data.model.language.CurrentLanguage
import com.example.spanishflashcards.data.remote.RetrofitInstance
import com.example.spanishflashcards.data.model.subject.EditSubject
import com.example.spanishflashcards.data.model.subject.Subject
import kotlinx.coroutines.launch
import com.example.spanishflashcards.ui.utils.AppScreen
import com.example.spanishflashcards.ui.utils.LoadError
import com.example.spanishflashcards.ui.utils.ScreenTitle
import com.example.spanishflashcards.ui.theme.ButtonShape
import com.example.spanishflashcards.ui.theme.CustomColors
import com.example.spanishflashcards.ui.theme.primaryButtonColors
import com.example.spanishflashcards.ui.theme.appTextFieldColors

@Composable
fun ViewSubjectEditDelete(subjectId: Long, onBack: () -> Unit, onDelete: () -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()


    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }


    // As loaded from the API - reloaded after a rotation
    var loadedSubject by remember {
        mutableStateOf<Subject?>(null)
    }

    // The user's edit. Saveable so it survives rotating the screen; null until loaded.
    var subjectName by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val subject = loadedSubject?.let {
        it.copy(subjectName = subjectName ?: it.subjectName)
    }

    var loadFailed by remember {
        mutableStateOf(false)
    }

    // Bumped by the Retry button to run the load again
    var loadAttempt by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(subjectId, loadAttempt) {
        loadFailed = false
        try {
            loadedSubject = RetrofitInstance.api.getSubject(CurrentLanguage.pathSegment, subjectId)
        } catch (e: Exception) {
            e.printStackTrace()
            loadFailed = true
        }
    }

    // Fields are only usable once the subject has loaded and while nothing is saving
    val canEdit = !isLoading && subject != null

    AppScreen(onBack = onBack, backEnabled = !isLoading) {

        if (loadFailed && subject == null) {
            LoadError(
                message = "Unable to load this subject. Please try again.",
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

            ScreenTitle(text = "Edit Subject")

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = subject?.subjectName?: "",
                onValueChange = {
                    subjectName = it
                },
                label = {
                    Text("Subject")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = canEdit,
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

                        // Edit subject handler
                        coroutineScope.launch {
                            isLoading = true
                            errorMessage = ""

                            val result = hendleSubjectEdit(subjectId, subject)

                            isLoading = false

                            if (result) {
                                onBack()
                            } else {
                                errorMessage = "Unable to edit subject."
                            }
                        }

                    },
                    enabled = canEdit,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = ButtonShape,
                    colors = primaryButtonColors()
                ) {
                    Text("Edit")
                }

                Button(
                    onClick = {
                        onDelete()
                    },
                    enabled = canEdit,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = ButtonShape,
                    colors = primaryButtonColors()
                ) {
                    Text("Delete")
                }

            }







        }
    }

}

suspend fun hendleSubjectEdit(subjectId: Long, subject: Subject?): Boolean {
    return try {
        if (subject == null) return false
        // The API only accepts the new name - id and language cannot be changed
        RetrofitInstance.api.updateSubject(
            language = CurrentLanguage.pathSegment,
            id = subjectId,
            subject = EditSubject(subjectName = subject.subjectName),
        )
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}