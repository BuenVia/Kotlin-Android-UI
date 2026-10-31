package com.example.spanishflashcards.ui.screens.vocab

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
import com.example.spanishflashcards.data.model.vocab.CreateVocab
import kotlinx.coroutines.launch
import com.example.spanishflashcards.ui.utils.AppScreen
import com.example.spanishflashcards.ui.utils.ScreenTitle
import com.example.spanishflashcards.ui.theme.CustomColors
import com.example.spanishflashcards.ui.theme.ButtonShape
import com.example.spanishflashcards.ui.theme.primaryButtonColors
import com.example.spanishflashcards.ui.theme.appTextFieldColors

@Composable
fun CreateVocabulary(subjectId: Long, onBack: () -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    // Saveable so typed text survives rotating the screen
    var term by rememberSaveable {
        mutableStateOf("")
    }

    var translation by rememberSaveable {
        mutableStateOf("")
    }

    val vocabVal = CreateVocab(
        term = term,
        translation = translation,
        subjectId = subjectId
    )

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
            ScreenTitle(text = "Create Vocabulary")

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Add a ${CurrentLanguage.language.displayName} word and its English translation.",
                fontSize = 14.sp,
                color = CustomColors.secondaryTextColor
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Word in the language being learned
            TextField(
                value = vocabVal.term,
                onValueChange = {
                    term = it
                    errorMessage = ""
                },
                label = {
                    Text("${CurrentLanguage.language.displayName} word")
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

            Spacer(modifier = Modifier.height(16.dp))

            // English translation
            TextField(
                value = vocabVal.translation,
                onValueChange = {
                    translation = it
                    errorMessage = ""
                },
                label = {
                    Text("English translation")
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

                    if (vocabVal.term.isBlank() || vocabVal.translation.isBlank()) {
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
        if (vocabVal.term.isBlank() || vocabVal.translation.isBlank()) {
            false
        } else {
            RetrofitInstance.api.createVocab(CurrentLanguage.pathSegment, vocabVal)
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