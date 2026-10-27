package com.example.spanishflashcards.ui.screens.vocab

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
import com.example.spanishflashcards.data.remote.RetrofitInstance
//import com.example.spanishflashcards.data.model.vocab.Vocab
import com.example.spanishflashcards.data.model.vocab.EditVocab
import kotlinx.coroutines.launch
import com.example.spanishflashcards.ui.utils.AppScreen
import com.example.spanishflashcards.ui.utils.ScreenTitle
import com.example.spanishflashcards.ui.theme.ButtonShape
import com.example.spanishflashcards.ui.theme.primaryButtonColors
import com.example.spanishflashcards.ui.theme.appTextFieldColors

@Composable
fun ViewVocabFunction(vocabId: Long, onBack: () -> Unit, onDelete: () -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()


    var vocab by remember {
        mutableStateOf<EditVocab?>(null)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    LaunchedEffect(vocabId) {
        try {
            vocab = RetrofitInstance.api.getVocabById(vocabId)
            println(vocab)
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

            ScreenTitle(text = "Edit Vocabulary")

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier.padding(10.dp),
//                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextField(
                    value = vocab?.esp ?: "",
                    onValueChange = {
                            newVal ->
                        vocab = vocab?.copy(esp = newVal)
                    },
                    label = {
                        Text("Vocab")
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

                TextField(
                    value = vocab?.eng ?: "",
                    onValueChange = {
                            newVal ->
                        vocab = vocab?.copy(eng = newVal)
                    },
                    label = {
                        Text("Translation")
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

                            if (vocab?.esp?.isBlank() == true || vocab?.eng?.isBlank() == true) {
                                errorMessage = "Please complete both fields."
                            } else {
                                coroutineScope.launch {
                                    isLoading = true
                                    errorMessage = ""

                                    val result = handleVocabEdit(vocabId, vocab)

                                    isLoading = false

                                    if (result) {
                                        onBack()
                                    } else {
                                        errorMessage = "Unable to edit vocab."
                                    }
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

}


suspend fun handleVocabEdit(vocabId: Long, vocabVal: EditVocab?): Boolean {
    return try {
        RetrofitInstance.api.editVocab(
            id = vocabId,
            vocab = vocabVal,
        )
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
