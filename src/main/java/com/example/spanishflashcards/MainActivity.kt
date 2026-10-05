package com.example.spanishflashcards

import android.os.Bundle
import android.view.RoundedCorner
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.exam.ExamFunction
import com.example.spanishflashcards.learn.LearnFunction
import com.example.spanishflashcards.ui.theme.SpanishFlashcardsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpanishFlashcardsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    mainHandler(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

enum class Screen {
    LEARN,
    EXAM
}

@Composable
fun mainHandler(modifier: Modifier = Modifier) {

    var userChoice by remember {
        mutableStateOf<Screen?>(null)
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        if (userChoice == null) {
            Column(
                modifier = Modifier.padding(32.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { userChoice = Screen.LEARN },
                    modifier = Modifier.height(40.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 0.dp
                    )
                ) {
                    Text("Learn")
                }

                Spacer( modifier = Modifier.height(20.dp))

                Button(onClick = { userChoice = Screen.EXAM },
                    modifier = Modifier.height(40.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Cyan,
                        contentColor = Color.Magenta
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 0.dp
                    )
                ) {
                    Text("Exam")
                }
            }
        }

    }

    when (userChoice) {
        Screen.LEARN -> LearnFunction(onBack = { userChoice = null })
        Screen.EXAM -> ExamFunction(onBack = { userChoice = null })
        null -> {}
    }
}