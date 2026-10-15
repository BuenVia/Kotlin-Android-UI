
package com.example.spanishflashcards.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.api.RetrofitInstance
import com.example.spanishflashcards.model.CreateVocab
import kotlinx.coroutines.launch

@Composable
fun CreateVocabulary(subjectId: Long, onBack: () -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    var vocabVal by remember(subjectId) {
        mutableStateOf(
            CreateVocab(
                esp = "",
                eng = "",
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

    val primaryColor = Color(0xFF4F46E5)
    val backgroundColor = Color(0xFFF8F9FC)
    val textColor = Color(0xFF1F2937)
    val secondaryTextColor = Color(0xFF6B7280)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp)
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

            // Heading
            Text(
                text = "Create Vocabulary",
                fontSize = 26.sp,
                color = textColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Add a Spanish word and its English translation.",
                fontSize = 14.sp,
                color = secondaryTextColor
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Spanish word
            TextField(
                value = vocabVal.esp,
                onValueChange = {
                    vocabVal = vocabVal.copy(esp = it)
                    errorMessage = ""
                },
                label = {
                    Text("Spanish word")
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
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        // Move focus to the next field using the keyboard.
                    }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // English translation
            TextField(
                value = vocabVal.eng,
                onValueChange = {
                    vocabVal = vocabVal.copy(eng = it)
                    errorMessage = ""
                },
                label = {
                    Text("English translation")
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

            Spacer(modifier = Modifier.height(16.dp))

            // Error feedback
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    fontSize = 14.sp,
                    color = Color(0xFFB91C1C)
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Create button
            Button(
                onClick = {
                    keyboardController?.hide()

                    if (vocabVal.esp.isBlank() || vocabVal.eng.isBlank()) {
                        errorMessage = "Please complete both fields."
                    } else {
                        coroutineScope.launch {
                            isLoading = true
                            errorMessage = ""

                            val result = handleVocabSubmit(vocabVal)

                            isLoading = false

                            if (result) {
                                onBack()
                            } else {
                                errorMessage =
                                    "Unable to create vocabulary. Please try again."
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
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Create Vocabulary",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

suspend fun handleVocabSubmit(vocabVal: CreateVocab): Boolean {
    return try {
        if (vocabVal.esp.isBlank() || vocabVal.eng.isBlank()) {
            false
        } else {
            RetrofitInstance.api.createVocab(vocabVal)
            true
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

@Composable
fun editVocab(vocabId: Long, onBack: () -> Unit) {
    // TODO write logic for editing a subject
}

@Composable
fun deleteVocab(vocabId: Long, onBack: () -> Unit) {
    // TODO write logic for deleting a subject
}