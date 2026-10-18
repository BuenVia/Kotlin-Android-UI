package com.example.spanishflashcards.subject

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
import com.example.spanishflashcards.api.RetrofitInstance
import com.example.spanishflashcards.mainHandler
import com.example.spanishflashcards.model.Subject
import com.example.spanishflashcards.vocab.ViewVocabDelete
import kotlinx.coroutines.launch

enum class EditDeleteSubjectScreen {
    DELETE,
    MAIN
}


@Composable
fun ViewSubjectEditDelete(subjectId: Long, onBack: () -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    val primaryColor = Color(0xFF4F46E5)
    val backgroundColor = Color(0xFFF8F9FC)
    val textColor = Color(0xFF1F2937)
    val secondaryTextColor = Color(0xFF6B7280)

    var editSubjectChoice by remember {
        mutableStateOf<EditDeleteSubjectScreen?>(null)
    }

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

    when (editSubjectChoice) {
        EditDeleteSubjectScreen.DELETE -> ViewVocabDelete(
            "SUBJECT",
            subjectId,
            onBack = { editSubjectChoice = EditDeleteSubjectScreen.MAIN }
        )

        EditDeleteSubjectScreen.MAIN -> mainHandler()

        null -> {
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

                    Text(text = ("Edit or Delete subject - " + subject?.id ?: "Loading"))




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

                        Button(
                            onClick = {
                                editSubjectChoice = EditDeleteSubjectScreen.DELETE
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryColor,
                                contentColor = Color.White,
                                disabledContainerColor = Color(0xFFC7C9F8),
                                disabledContentColor = Color.White
                            )
                        ) {
                            Text("Delete")
                        }

                    }







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