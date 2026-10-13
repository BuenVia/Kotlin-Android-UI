package com.example.spanishflashcards.subject

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
import com.example.spanishflashcards.Screen
import com.example.spanishflashcards.api.RetrofitInstance
import com.example.spanishflashcards.create.CreateVocabulary
import com.example.spanishflashcards.exam.ExamFunction
import com.example.spanishflashcards.learn.LearnFunction
import com.example.spanishflashcards.model.Subject
import com.example.spanishflashcards.model.Vocab


enum class LearnScreen {
    LEARN,
    EXAM,
    CREATE_VOCAB
}

@Composable
fun SubjectViewFunction(subjectId: Long, onBack: () -> Unit) {

    var learnChoice by remember {
        mutableStateOf<LearnScreen?>(null)
    }

    var subjectChoice by remember {
        mutableLongStateOf(0)
    }

    var subject by remember {
        mutableStateOf<Subject?>(null)
    }

    var vocabList: List<Vocab>? by remember {
        mutableStateOf(listOf< Vocab>())
    }

    LaunchedEffect(Unit) {
        try {
            subject = RetrofitInstance.api.getSubject(subjectId)
            vocabList = subject?.vocabs
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        // Back button
        Button(
            onClick = { onBack(); vocabList = listOf< Vocab >() },
            modifier = Modifier.align(Alignment.TopEnd).padding(25.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
        ) {
            Text("Back")
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Text(
                text = subject?.subjectName ?: "Loading...",
                fontSize = 18.sp
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = {
                        subjectChoice = subjectId
                        learnChoice = LearnScreen.LEARN
                        println(subjectId)
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
                        subjectChoice = subjectId
                        learnChoice = LearnScreen.EXAM
                        println(subjectId)
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

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    subjectChoice = subjectId
                    learnChoice = LearnScreen.CREATE_VOCAB
                    println(subjectId)
                },
            ) {
                Text("Create Vocab")
            }

            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                if (vocabList != null) {
                    for (vocab in vocabList) {
                        Text(text = vocab.esp)
                    }
                }
            }
        }

    }

    when (learnChoice) {
        LearnScreen.LEARN -> LearnFunction(
            subjectId,
            onBack = { learnChoice = null }
        )

        LearnScreen.EXAM -> ExamFunction(
            subjectId,
            onBack = { learnChoice = null }
        )

        LearnScreen.CREATE_VOCAB -> CreateVocabulary(
            subjectId,
            onBack = { learnChoice = null }
        )

        null -> {}
    }

}