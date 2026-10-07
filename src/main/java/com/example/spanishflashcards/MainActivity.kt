package com.example.spanishflashcards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.api.RetrofitInstance
import com.example.spanishflashcards.exam.ExamFunction
import com.example.spanishflashcards.learn.LearnFunction
import com.example.spanishflashcards.model.SubjectNames
import com.example.spanishflashcards.ui.theme.SpanishFlashcardsTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            SpanishFlashcardsTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    mainHandler(
                        modifier = Modifier.padding(innerPadding)
                    )
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

    var subjectChoice by remember {
        mutableLongStateOf(0)
    }

    var listSubjects by remember {
        mutableStateOf<List<SubjectNames>>(emptyList())
    }

    LaunchedEffect(Unit) {
        try {
            listSubjects = RetrofitInstance.api.getSubjectNames()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        if (userChoice == null) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Spanish Flashcards",
                    fontSize = 28.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Choose a subject",
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                for (sub in listSubjects) {

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Text(
                                text = sub.subjectName ?: "Loading...",
                                fontSize = 18.sp
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                Button(
                                    onClick = {
                                        subjectChoice = sub.id
                                        userChoice = Screen.LEARN
                                    },
                                    modifier = Modifier.height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF3F51B5),
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text("Learn")
                                }

                                Button(
                                    onClick = {
                                        subjectChoice = sub.id
                                        userChoice = Screen.EXAM
                                    },
                                    modifier = Modifier.height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFE8EAF6),
                                        contentColor = Color(0xFF3F51B5)
                                    )
                                ) {
                                    Text("Exam")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    when (userChoice) {
        Screen.LEARN -> LearnFunction(
            subjectChoice,
            onBack = { userChoice = null }
        )

        Screen.EXAM -> ExamFunction(
            subjectChoice,
            onBack = { userChoice = null }
        )

        null -> {}
    }
}