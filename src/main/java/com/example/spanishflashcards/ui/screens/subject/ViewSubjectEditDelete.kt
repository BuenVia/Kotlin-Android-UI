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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.spanishflashcards.data.remote.RetrofitInstance
import com.example.spanishflashcards.data.model.subject.Subject
import kotlinx.coroutines.launch
import com.example.spanishflashcards.ui.utils.AppScreen
import com.example.spanishflashcards.ui.utils.ScreenTitle
import com.example.spanishflashcards.ui.theme.ButtonShape
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

    AppScreen(onBack = onBack, backEnabled = !isLoading) {

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
                    subject = subject?.copy(subjectName = it)
                },
                label = {
                    Text("Subject")
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
                    enabled = !isLoading,
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
                    enabled = !isLoading,
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
        val res = RetrofitInstance.api.updateSubject(
            id = subjectId,
            subject = subject,
        )
        println(res)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}