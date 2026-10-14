
package com.example.spanishflashcards.exam

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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

    var vocabList by remember {
        mutableStateOf<List<Vocab>>(emptyList())
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

    val neutralColor = Color(0xFFE0E7FF)
    val primaryColor = Color(0xFF4F46E5)
    val textColor = Color(0xFF1F2937)
    val secondaryTextColor = Color(0xFF6B7280)
    val successColor = Color(0xFF15803D)
    val errorColor = Color(0xFFB91C1C)

    var feedbackColor by remember {
        mutableStateOf(neutralColor)
    }

    val lenVocabs = subject?.vocabs?.size ?: 0

    LaunchedEffect(subjectId) {
        try {
            subject = RetrofitInstance.api.getSubject(subjectId)
            vocabList = subject?.vocabs ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun checkAnswer(answer: String, correctAnswer: String) {
        if (answer.trim().equals(correctAnswer, ignoreCase = true)) {
            result = "Correct!"
            showNext = true
            feedbackColor = successColor
        } else {
            result = "Incorrect. Try again."
            feedbackColor = errorColor
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FC))
            .padding(24.dp)
    ) {

        // Back button
        Button(
            onClick = { onBack() },
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

            if (subject == null) {

                Text(
                    text = "Loading...",
                    fontSize = 18.sp,
                    color = secondaryTextColor
                )

            } else if (indexNum < lenVocabs) {

                // Heading
                Text(
                    text = subject?.subjectName ?: "",
                    fontSize = 26.sp,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Question ${indexNum + 1} of $lenVocabs",
                    fontSize = 14.sp,
                    color = secondaryTextColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Question card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .border(
                            width = 2.dp,
                            color = feedbackColor,
                            shape = RoundedCornerShape(24.dp)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = subject?.vocabs
                                ?.getOrNull(indexNum)?.eng
                                ?: "",
                            fontSize = 32.sp,
                            color = textColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Answer input
                TextField(
                    value = userAnswer,
                    onValueChange = {
                        userAnswer = it
                        result = ""
                        feedbackColor = neutralColor
                    },
                    label = {
                        Text("Your answer")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = feedbackColor,
                            shape = RoundedCornerShape(14.dp)
                        ),
                    textStyle = TextStyle(
                        color = textColor,
                        fontSize = 16.sp
                    ),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    enabled = !showNext,
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
                            if (!showNext) {
                                checkAnswer(
                                    userAnswer,
                                    subject?.vocabs
                                        ?.getOrNull(indexNum)?.esp ?: ""
                                )
                            }
                            keyboardController?.hide()
                        }
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Answer feedback
                Text(
                    text = result,
                    fontSize = 18.sp,
                    color = feedbackColor
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (!showNext) {

                    // Check answer
                    Button(
                        onClick = {
                            checkAnswer(
                                userAnswer,
                                subject?.vocabs
                                    ?.getOrNull(indexNum)?.esp ?: ""
                            )
                            keyboardController?.hide()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Check answer",
                            fontSize = 16.sp
                        )
                    }

                } else {

                    // Next question
                    Button(
                        onClick = {
                            indexNum++
                            userAnswer = ""
                            result = ""
                            showNext = false
                            feedbackColor = neutralColor
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Next",
                            fontSize = 16.sp
                        )
                    }
                }

            } else {

                // Exam finished
                Text(
                    text = "Exam completed!",
                    fontSize = 26.sp,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "You've answered all $lenVocabs questions.",
                    fontSize = 16.sp,
                    color = secondaryTextColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Vocabulary summary
                vocabList.forEach { vocab ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = vocab.eng,
                                fontSize = 16.sp,
                                color = textColor
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = vocab.esp,
                                fontSize = 16.sp,
                                color = primaryColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Return to Subject View
                Button(
                    onClick = { onBack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Back to Subject",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
