
package com.example.spanishflashcards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.data.remote.RetrofitInstance
import com.example.spanishflashcards.ui.screens.subject.CreateFunction
import com.example.spanishflashcards.data.model.subject.SubjectNames
import com.example.spanishflashcards.ui.screens.subject.SubjectAdminViewFunction
import com.example.spanishflashcards.ui.screens.subject.SubjectViewFunction
import com.example.spanishflashcards.ui.theme.SpanishFlashcardsTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Set Admin mode - for dev purposes only
        val adminMode = true

        setContent {
            SpanishFlashcardsTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color(0xFFF8F9FC)
                ) { innerPadding ->


                    if(adminMode == true) {
                        mainAdminHandler(
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        mainHandler(
                            modifier = Modifier.padding(innerPadding)
                        )
                    }

                }
            }
        }
    }
}

enum class Screen {
    CREATE,
    SUBJECT
}

@Composable
fun mainHandler(modifier: Modifier = Modifier) {

    var userChoice by remember {
        mutableStateOf<Screen?>(null)
    }

    var subjectChoice by remember {
        mutableLongStateOf(0L)
    }

    var listSubjects by remember {
        mutableStateOf<List<SubjectNames>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var refreshTrigger by remember {
        mutableIntStateOf(0)
    }

    val primaryColor = Color(0xFF4F46E5)
    val backgroundColor = Color(0xFFF8F9FC)
    val textColor = Color(0xFF1F2937)
    val secondaryTextColor = Color(0xFF6B7280)

    LaunchedEffect(refreshTrigger) {
        isLoading = true
        errorMessage = ""

        try {
            listSubjects = RetrofitInstance.api.getSubjectNames()
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = "Unable to load subjects. Please try again."
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {

        if (userChoice == null) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // App heading
                Text(
                    text = "Spanish Flashcards",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Build your vocabulary, one card at a time.",
                    fontSize = 15.sp,
                    color = secondaryTextColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Subject list heading
                Text(
                    text = "Your subjects",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable subject list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(listSubjects) { sub ->

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            )
                        ) {

                            Button(
                                onClick = {
                                    subjectChoice = sub.id
                                    userChoice = Screen.SUBJECT
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = textColor
                                )
                            ) {

                                Text(
                                    text = sub.subjectName ?: "Unnamed subject",
                                    modifier = Modifier.fillMaxWidth(),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = primaryColor
                                )
                            }
                        }
                    }

                    if (errorMessage.isNotEmpty()) {
                        item {
                            Text(
                                text = errorMessage,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                fontSize = 14.sp,
                                color = Color(0xFFB91C1C),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    if (!isLoading &&
                        errorMessage.isEmpty() &&
                        listSubjects.isEmpty()
                    ) {
                        item {
                            Text(
                                text = "No subjects yet. Create your first one!",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                fontSize = 15.sp,
                                color = secondaryTextColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    // Screen navigation
    when (userChoice) {

        Screen.SUBJECT -> SubjectViewFunction(
            subjectChoice,
            onBack = {
                userChoice = null
                refreshTrigger++
            }
        )

        Screen.CREATE -> CreateFunction(
            onBack = {
                userChoice = null
                refreshTrigger++
            }
        )

        null -> {}
    }
}

@Composable
fun mainAdminHandler(modifier: Modifier = Modifier) {

    var userChoice by remember {
        mutableStateOf<Screen?>(null)
    }

    var subjectChoice by remember {
        mutableLongStateOf(0L)
    }

    var listSubjects by remember {
        mutableStateOf<List<SubjectNames>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var refreshTrigger by remember {
        mutableIntStateOf(0)
    }

    val primaryColor = Color(0xFF4F46E5)
    val backgroundColor = Color(0xFFF8F9FC)
    val textColor = Color(0xFF1F2937)
    val secondaryTextColor = Color(0xFF6B7280)

    LaunchedEffect(refreshTrigger) {
        isLoading = true
        errorMessage = ""

        try {
            listSubjects = RetrofitInstance.api.getSubjectNames()
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = "Unable to load subjects. Please try again."
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {

        if (userChoice == null) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // App heading
                Text(
                    text = "Spanish Flashcards",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Build your vocabulary, one card at a time.",
                    fontSize = 15.sp,
                    color = secondaryTextColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Subject list heading
                Text(
                    text = "Your subjects",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable subject list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(listSubjects) { sub ->

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            )
                        ) {

                            Button(
                                onClick = {
                                    subjectChoice = sub.id
                                    userChoice = Screen.SUBJECT
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = textColor
                                )
                            ) {

                                Text(
                                    text = sub.subjectName ?: "Unnamed subject",
                                    modifier = Modifier.fillMaxWidth(),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = primaryColor
                                )
                            }
                        }
                    }

                    if (errorMessage.isNotEmpty()) {
                        item {
                            Text(
                                text = errorMessage,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                fontSize = 14.sp,
                                color = Color(0xFFB91C1C),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    if (!isLoading &&
                        errorMessage.isEmpty() &&
                        listSubjects.isEmpty()
                    ) {
                        item {
                            Text(
                                text = "No subjects yet. Create your first one!",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                fontSize = 15.sp,
                                color = secondaryTextColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Create subject button
                Button(
                    onClick = {
                        userChoice = Screen.CREATE
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "+  Create subject",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    // Screen navigation
    when (userChoice) {

        Screen.SUBJECT -> SubjectAdminViewFunction(
            subjectChoice,
            onBack = {
                userChoice = null
                refreshTrigger++
            }
        )

        Screen.CREATE -> CreateFunction(
            onBack = {
                userChoice = null
                refreshTrigger++
            }
        )

        null -> {}
    }
}

