
package com.example.spanishflashcards.ui.screens.practice

import androidx.compose.foundation.border
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.data.model.language.CurrentLanguage
import com.example.spanishflashcards.data.remote.RetrofitInstance
import com.example.spanishflashcards.data.model.subject.Subject
import com.example.spanishflashcards.data.model.vocab.Vocab
import com.example.spanishflashcards.ui.theme.CustomColors
import com.example.spanishflashcards.ui.utils.AppScreen
import com.example.spanishflashcards.ui.utils.LoadError
import com.example.spanishflashcards.ui.utils.ScreenTitle
import com.example.spanishflashcards.ui.theme.ButtonShape
import com.example.spanishflashcards.ui.theme.primaryButtonColors
import com.example.spanishflashcards.ui.theme.appTextFieldColors

@Composable
fun PracticeView(subjectId: Long, onBack: () -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current

    var subject by remember {
        mutableStateOf<Subject?>(null)
    }

    var vocabList by remember {
        mutableStateOf<List<Vocab>>(emptyList())
    }

    // Progress is saveable so rotating the screen doesn't restart the practice.
    // The subject itself is reloaded from the API after a rotation.
    var indexNum by rememberSaveable {
        mutableIntStateOf(0)
    }

    var userAnswer by rememberSaveable {
        mutableStateOf("")
    }

    var result by rememberSaveable {
        mutableStateOf("")
    }

    var showNext by rememberSaveable {
        mutableStateOf(false)
    }

    var loadFailed by remember {
        mutableStateOf(false)
    }

    // Bumped by the Retry button to run the load again
    var loadAttempt by remember {
        mutableIntStateOf(0)
    }

    // Derived from the answer state rather than stored, so it is restored along with it
    val feedbackColor = when {
        result.isEmpty() -> CustomColors.accentColor
        showNext -> CustomColors.successColor
        else -> CustomColors.errorColor
    }

    val lenVocabs = subject?.vocabs?.size ?: 0

    LaunchedEffect(subjectId, loadAttempt) {
        loadFailed = false
        try {
            subject = RetrofitInstance.api.getSubject(CurrentLanguage.pathSegment, subjectId)
            vocabList = subject?.vocabs ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            loadFailed = true
        }
    }

    fun checkAnswer(answer: String, correctAnswer: String) {
        if (answer.trim().equals(correctAnswer, ignoreCase = true)) {
            result = "Correct!"
            showNext = true
        } else {
            result = "Incorrect. Try again."
        }
    }

    AppScreen(onBack = onBack) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (subject == null && loadFailed) {

                LoadError(
                    message = "Unable to load this subject. Please try again.",
                    onRetry = { loadAttempt++ }
                )

            } else if (subject == null) {

                Text(
                    text = "Loading...",
                    fontSize = 18.sp,
                    color = CustomColors.secondaryTextColor
                )

            } else if (indexNum < lenVocabs) {

                // Heading
                ScreenTitle(text = subject?.subjectName ?: "")

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Question ${indexNum + 1} of $lenVocabs",
                    fontSize = 14.sp,
                    color = CustomColors.secondaryTextColor
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
                        containerColor = CustomColors.surfaceColor
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
                                ?.getOrNull(indexNum)?.translation
                                ?: "",
                            fontSize = 32.sp,
                            color = CustomColors.textColor
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
                    },
                    label = {
                        Text("Your answer")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = feedbackColor,
                            shape = ButtonShape
                        ),
                    textStyle = TextStyle(
                        color = CustomColors.textColor,
                        fontSize = 16.sp
                    ),
                    shape = ButtonShape,
                    singleLine = true,
                    enabled = !showNext,
                    colors = appTextFieldColors(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (!showNext) {
                                checkAnswer(
                                    userAnswer,
                                    subject?.vocabs
                                        ?.getOrNull(indexNum)?.term ?: ""
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
                                    ?.getOrNull(indexNum)?.term ?: ""
                            )
                            keyboardController?.hide()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = ButtonShape,
                        colors = primaryButtonColors()
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
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = ButtonShape,
                        colors = primaryButtonColors()
                    ) {
                        Text(
                            text = "Next",
                            fontSize = 16.sp
                        )
                    }
                }

            } else {

                // Practice finished
                ScreenTitle(text = "Practice completed!")

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "You've answered all $lenVocabs questions.",
                    fontSize = 16.sp,
                    color = CustomColors.secondaryTextColor
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
                            containerColor = CustomColors.surfaceColor
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = vocab.translation,
                                fontSize = 16.sp,
                                color = CustomColors.textColor
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = vocab.term,
                                fontSize = 16.sp,
                                color = CustomColors.primaryColor
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
                    shape = ButtonShape,
                    colors = primaryButtonColors()
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
