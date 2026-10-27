package com.example.spanishflashcards.ui.screens.vocab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.data.remote.RetrofitInstance
import com.example.spanishflashcards.ui.theme.secondaryButtonColors
import com.example.spanishflashcards.ui.utils.AppScreen
import com.example.spanishflashcards.ui.utils.ScreenTitle
import kotlinx.coroutines.launch
import com.example.spanishflashcards.ui.theme.CustomColors
import com.example.spanishflashcards.ui.theme.ButtonShape
import com.example.spanishflashcards.ui.theme.primaryButtonColors

@Composable
fun ViewVocabDelete(delType: DeleteType, itemId: Long, onDeleted: () -> Unit, onCancel: () -> Unit) {

    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }


    AppScreen(onBack = onCancel, backEnabled = !isLoading) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ScreenTitle(text = "Confirm Deletion")

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Are you sure you want to delete? This action cannot be undone.",
                fontSize = 14.sp,
                color = CustomColors.secondaryTextColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {

                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = ""
                        val (result, itemName) = when (delType) {
                            DeleteType.VOCAB -> handleVocabDelete(itemId) to "vocab"
                            DeleteType.SUBJECT -> handleSubjectDelete(itemId) to "subject"
                        }
                        isLoading = false

                        if (result) {
                            onDeleted()
                        } else {
                            errorMessage =
                                "Unable to delete the $itemName. Please try again."
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
                Text("Confirm Deletion")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    onCancel()
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = ButtonShape,
                colors = secondaryButtonColors()
            ) {
                Text("Cancel")
            }

        }
    }

}

suspend fun handleVocabDelete(vocabId: Long): Boolean {
    return try {
        val res = RetrofitInstance.api.deleteVocab(vocabId)
        res.isSuccessful
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

suspend fun handleSubjectDelete(subjectId: Long): Boolean {
    return try {
        val res = RetrofitInstance.api.deleteSubject((subjectId))
        res.isSuccessful

    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}