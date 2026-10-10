package com.example.spanishflashcards.exam

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.api.RetrofitInstance
import com.example.spanishflashcards.model.Subject
import com.example.spanishflashcards.model.Vocab

@Composable
fun ExamFunction(subjectId: Long, onBack: () -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current

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

    var indexNum by remember {
        mutableIntStateOf(0)
    }

    var userAnswer by remember {
        mutableStateOf("")
    }

    var result by remember {
        mutableStateOf("")
    }

    var showNext by remember {
        mutableStateOf(false)
    }

    var colorScheme by remember { mutableStateOf(Color(0xFFdddddd)) }

    val lenVocabs = subject?.vocabs?.size ?: 0

    fun checkAnswer(userAnswer: String, correctAnswer: String) {
        if (userAnswer.trim().equals(correctAnswer, ignoreCase = true)) {
            result = "Correct"
            showNext = true
            colorScheme = Color(0xFF007722)
        } else {
            result = "Incorrect"
            colorScheme = Color(0xFF882222)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        // Back button
        Button(
            onClick = { onBack() },
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
        )
        {

            if (indexNum < lenVocabs) {

                // Question
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp).border(
                            width = 2.dp,
                            color = colorScheme,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = subject?.vocabs?.get(indexNum)?.eng
                                ?: "Loading...",
                            fontSize = 36.sp,
                            color = colorScheme
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Answer input
                TextField(
                    value = userAnswer,
                    onValueChange = {
                        userAnswer = it
                    },
                    label = {
                        Text(
                            text = "Your answer",
                            color = colorScheme
                        )
                    },
                    modifier = Modifier.fillMaxWidth().border(
                        width = 2.dp,
                        color = colorScheme,
                        shape = RoundedCornerShape(12.dp)
                    ),
                    textStyle = TextStyle(color = colorScheme),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            checkAnswer(
                                userAnswer,
                                subject?.vocabs?.get(indexNum)?.esp ?: ""
                            )

                            keyboardController?.hide()
                        }
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Result
                Text(
                    text = result,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Check / Next / Finish
                if (!showNext) {

                    Button(
                        onClick = {
                            checkAnswer(
                                userAnswer,
                                subject?.vocabs?.get(indexNum)?.esp ?: ""
                            )

                            keyboardController?.hide()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme,
                            contentColor = Color(0xFF333333)
                        )
                    ) {
                        Text(
                            text = "Check answer",
                            fontSize = 16.sp
                        )
                    }

                } else if (indexNum < lenVocabs) {

                    Button(
                        onClick = {
                            indexNum += 1

                            // Clear previous answer
                            userAnswer = ""

                            // Clear previous result
                            result = ""

                            showNext = false

                            colorScheme = Color(0xFFdddddd)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Next",
                            fontSize = 16.sp
                        )
                    }

                }
//                else {
//
//                    if (vocabList != null) {
//                        for (vocab in vocabList) {
//                            Text(text = vocab.esp + " / " + vocab.eng)
//                            Spacer(modifier = Modifier.height(10.dp))
//                        }
//                    }
//
//                    Button(
//                        onClick = {
//                            onBack()
//                        },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(52.dp),
//                        shape = RoundedCornerShape(14.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color(0xFFE8EAF6),
//                            contentColor = Color(0xFF3F51B5)
//                        )
//                    )
//                    {
//                        Text(
//                            text = "Finish",
//                            fontSize = 16.sp
//                        )
//                    }
//                }

            } else {
                if (vocabList != null) {
                    for (vocab in vocabList) {
                        Text(text = vocab.esp + " / " + vocab.eng)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                Button(
                    onClick = {
                        onBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE8EAF6),
                        contentColor = Color(0xFF3F51B5)
                    )
                ) {
                    Text(
                        text = "Finish",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
