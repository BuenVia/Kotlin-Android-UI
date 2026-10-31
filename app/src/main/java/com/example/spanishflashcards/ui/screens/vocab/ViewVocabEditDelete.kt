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
import com.example.spanishflashcards.data.model.vocab.EditVocab
import kotlinx.coroutines.launch
import com.example.spanishflashcards.ui.utils.AppScreen
import com.example.spanishflashcards.ui.utils.LoadError
import com.example.spanishflashcards.ui.utils.ScreenTitle
import com.example.spanishflashcards.ui.theme.ButtonShape
import com.example.spanishflashcards.ui.theme.CustomColors
import com.example.spanishflashcards.ui.theme.primaryButtonColors
import com.example.spanishflashcards.ui.theme.appTextFieldColors

@Composable
fun ViewVocabFunction(vocabId: Long, onBack: () -> Unit, onDelete: () -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()


    // As loaded from the API - reloaded after a rotation
    var loadedVocab by remember {
        mutableStateOf<EditVocab?>(null)
    }

    // The user's edits. Saveable so they survive rotating the screen; null until loaded.
    var term by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var translation by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val vocab = loadedVocab?.let {
        it.copy(term = term ?: it.term, translation = translation ?: it.translation)
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

    LaunchedEffect(vocabId, loadAttempt) {
        loadFailed = false
        try {
            loadedVocab = RetrofitInstance.api.getVocabById(CurrentLanguage.pathSegment, vocabId)
        } catch (e: Exception) {
            e.printStackTrace()
            loadFailed = true
        }
    }

    // Fields are only usable once the vocab has loaded and while nothing is saving
    val canEdit = !isLoading && vocab != null

    AppScreen(onBack = onBack, backEnabled = !isLoading) {

        if (loadFailed && vocab == null) {
            LoadError(
                message = "Unable to load this vocabulary. Please try again.",
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

            ScreenTitle(text = "Edit Vocabulary")

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier.padding(10.dp),
//                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextField(
                    value = vocab?.term ?: "",
                    onValueChange = {
                            newVal ->
                        term = newVal
                    },
                    label = {
                        Text("Vocab")
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

                TextField(
                    value = vocab?.translation ?: "",
                    onValueChange = {
                            newVal ->
                        translation = newVal
                    },
                    label = {
                        Text("Translation")
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

                            if (vocab?.term?.isBlank() == true || vocab?.translation?.isBlank() == true) {
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

}


suspend fun handleVocabEdit(vocabId: Long, vocabVal: EditVocab?): Boolean {
    return try {
        RetrofitInstance.api.editVocab(
            language = CurrentLanguage.pathSegment,
            id = vocabId,
            vocab = vocabVal,
        )
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
