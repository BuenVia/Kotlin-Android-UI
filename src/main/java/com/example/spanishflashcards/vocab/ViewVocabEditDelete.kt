package com.example.spanishflashcards.vocab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.spanishflashcards.api.RetrofitInstance
import com.example.spanishflashcards.model.Vocab

@Composable
fun ViewVocabFunction(vocabId: Long, onBack: () -> Unit) {

    var vocab by remember {
        mutableStateOf<Vocab?>(null)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(vocabId) {
        try {
            vocab = RetrofitInstance.api.getVocabById(vocabId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

            Text(text = "Edit or Delete vocab - " + vocabId)

            Row(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = vocab?.esp ?: "...")
                Text(text = vocab?.eng ?: "...")
            }
        }
    }
}