package com.example.spanishflashcards.vocab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.spanishflashcards.api.RetrofitInstance
import kotlinx.coroutines.launch

@Composable
fun ViewVocabDelete(delType: String, itemId: Long, onBack: () -> Unit) {

    val coroutineScope = rememberCoroutineScope()

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
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Are you sure you want to delete? This action cannot be undone.")

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {

                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = ""
                        if (delType == "VOCAB") {
                            val result = handleVocabDelete(itemId)
                            isLoading = false

                            if (result) {
                                onBack()
                            } else {
                                errorMessage =
                                    "Unable to delete the vocab. Please try again."
                            }
                        } else if (delType == "SUBJECT") {
                            val result = handleSubjectDelete(itemId)
                            isLoading = false

                            if (result) {
                                onBack()
                            } else {
                                errorMessage =
                                    "Unable to delete the subject. Please try again."
                            }
                        } else {
                            onBack()
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
                Text("Confirm Deletion")
            }

            Button(
                onClick = {
                    onBack()
                }
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