package com.example.spanishflashcards.learn

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.model.Subject
import com.example.spanishflashcards.api.RetrofitInstance
import com.example.spanishflashcards.model.Vocab

@SuppressLint("MissingColorAlphaChannel")
@Composable
fun LearnFunction(subjectId: Long, onBack: () -> Unit) {

    var subject by remember {
        mutableStateOf<Subject?>(null)
    }

    var vocabList: List<Vocab>? by remember {
        mutableStateOf(listOf< Vocab>())
    }

    var indexNum by remember {
        mutableIntStateOf(0)
    }

    var answerVisible by remember {
        mutableStateOf(false)
    }

    var failedVocab by remember {
        mutableStateOf(listOf< Vocab >())
    }

    val lenVocabs = subject?.vocabs?.size ?: 0

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
    )
    {

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

            if (indexNum < lenVocabs) {

                // Subject
                Text(
                    text = subject?.subjectName ?: "Loading...",
                    fontSize = 22.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Vocab
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                )
                {


                    Button(
                        onClick = { answerVisible = !answerVisible },
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF222222),
                            contentColor = Color.White
                        )
                    ) {

                        Text(
                            text = if (answerVisible) {
                                subject?.vocabs?.get(indexNum)?.esp ?: "Loading..."
                            } else {
                                subject?.vocabs?.get(indexNum)?.eng ?: "Loading..."
                            },
                            fontSize = 36.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                )
                {

                    if (answerVisible) {

                        Button(
                            onClick = {
                                indexNum += 1
                                answerVisible = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3F51B5),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Good",
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                failedVocab = (failedVocab + vocabList?.get(indexNum)) as List<Vocab>
                                indexNum += 1
                                answerVisible = false;
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE8EAF6),
                                contentColor = Color(0xFF3F51B5)
                            )
                        ) {
                            Text(
                                text = "Bad",
                                fontSize = 16.sp
                            )
                        }

                    } else {
                        // Empty Button as placeholder
                        Button(
                            onClick = {},
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.Transparent
                            )
                        ) {
                            Text(text = "")
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Empty Button as placeholder
                        Button(
                            onClick = {},
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.Transparent
                            )
                        ) {
                            Text(text = "")
                        }

                    }
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))

                if (failedVocab?.size?:0 > 0) {
                    Text("List of failure!")
                    for (vocab in failedVocab) {
                        Button(
                            onClick = { answerVisible = !answerVisible },
                            modifier = Modifier.height(36.dp).fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF222222),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(0.dp),
                            border = BorderStroke(1.dp, Color(0xFF777777)),

                        ) {

                            Text(
                                text = if (answerVisible) {
                                    vocab.esp
                                } else {
                                    vocab.eng
                                },
//                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                } else {
                    Text("FINISHED")
                }

                Spacer(Modifier.height(10.dp))

                // Back button
                Button(
                    onClick = { onBack(); vocabList = listOf< Vocab >(); subject = null},
                    modifier = Modifier.height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3F51B5),
                        contentColor = Color.White
                    ),
                ) {
                    Text("Back")
                }
            }
        }

    }

}


