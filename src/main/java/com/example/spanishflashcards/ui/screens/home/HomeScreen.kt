package com.example.spanishflashcards.ui.screens.home

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.BuildConfig
import com.example.spanishflashcards.data.model.subject.SubjectNames
import com.example.spanishflashcards.data.remote.RetrofitInstance
import com.example.spanishflashcards.ui.theme.ButtonShape
import com.example.spanishflashcards.ui.theme.CardShape
import com.example.spanishflashcards.ui.theme.CustomColors
import com.example.spanishflashcards.ui.theme.primaryButtonColors
import com.example.spanishflashcards.ui.theme.surfaceButtonColors
import com.example.spanishflashcards.ui.utils.AppScreen

@Composable
fun HomeScreen(
    isAdmin: Boolean,
    onSubjectClick: (subjectId: Long) -> Unit,
    onCreateSubject: () -> Unit,
    modifier: Modifier = Modifier
) {

    var listSubjects by remember {
        mutableStateOf<List<SubjectNames>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
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

    AppScreen(modifier = modifier) {

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
                color = CustomColors.textColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Build your vocabulary, one card at a time.",
                fontSize = 15.sp,
                color = CustomColors.secondaryTextColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Subject list heading
            Text(
                text = "Your subjects",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = CustomColors.textColor
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

                    Button(
                        onClick = { onSubjectClick(sub.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        shape = CardShape,
                        colors = surfaceButtonColors(),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 2.dp
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

                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = CustomColors.primaryColor
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
                            color = CustomColors.errorColor,
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
                            color = CustomColors.secondaryTextColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (isAdmin) {

                Spacer(modifier = Modifier.height(20.dp))

                // Create subject button
                Button(
                    onClick = { onCreateSubject() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = ButtonShape,
                    colors = primaryButtonColors()
                ) {
                    Text(
                        text = "+  Create subject",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Version footer - the value comes from versionName in build.gradle.kts
            Text(
                text = "Version ${BuildConfig.VERSION_NAME}",
                fontSize = 12.sp,
                color = CustomColors.secondaryTextColor,
                textAlign = TextAlign.Center
            )
        }
    }
}
