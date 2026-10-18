package com.example.spanishflashcards.vocab

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
//import com.example.spanishflashcards.model.Vocab
import com.example.spanishflashcards.model.EditVocab
import com.example.spanishflashcards.subject.SubjectViewFunction
import kotlinx.coroutines.launch

enum class EditDeleteScreen {
    DELETE,
    SUBJECT
}

@Composable
fun ViewVocabFunction(vocabId: Long, subjectId: Long, onBack: () -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    val primaryColor = Color(0xFF4F46E5)
    val backgroundColor = Color(0xFFF8F9FC)
    val textColor = Color(0xFF1F2937)
    val secondaryTextColor = Color(0xFF6B7280)

    var editChoice by remember {
        mutableStateOf<EditDeleteScreen?>(null)
    }

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

    when (editChoice) {
        EditDeleteScreen.DELETE -> ViewVocabDelete(
            "VOCAB",
            vocabId,
            onBack = { editChoice = EditDeleteScreen.SUBJECT }
        )

        EditDeleteScreen.SUBJECT -> SubjectViewFunction(
            subjectId,
            onBack = {
                editChoice = null
            }

        )

        null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                // Back button
                Button(
                    onClick = { editChoice = EditDeleteScreen.SUBJECT },
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

                    Text(text = "Edit or Delete vocab - " + vocabId)

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
                                    editChoice = EditDeleteScreen.DELETE
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
