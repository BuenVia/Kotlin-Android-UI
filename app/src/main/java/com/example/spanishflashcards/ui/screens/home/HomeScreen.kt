package com.example.spanishflashcards.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.spanishflashcards.data.model.language.CurrentLanguage
import com.example.spanishflashcards.data.remote.RetrofitInstance
import com.example.spanishflashcards.ui.theme.CustomColors
import com.example.spanishflashcards.ui.utils.AdminLink
import com.example.spanishflashcards.ui.utils.AppScreen
import com.example.spanishflashcards.ui.utils.EditModeToggle
import com.example.spanishflashcards.ui.utils.LoadError

@Composable
fun HomeScreen(
    isAdmin: Boolean,
    onEditModeChange: ((Boolean) -> Unit)?,
    onChangeLanguage: () -> Unit,
    onSubjectClick: (subjectId: Long) -> Unit,
    onCreateSubject: () -> Unit,
    onCreateCategory: () -> Unit,
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

    // Newest first - the API returns database order, but sort so it never depends on that
    val sortedSubjects = remember(listSubjects) { listSubjects.sortedByDescending { it.id } }

    val language = CurrentLanguage.language

    // Bumped by the Retry button to run the load again
    var loadAttempt by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(language, loadAttempt) {
        isLoading = true
        errorMessage = ""

        try {
            listSubjects = RetrofitInstance.api.getSubjectNames(language.pathSegment)
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = "Unable to load subjects. Please try again."
        } finally {
            isLoading = false
        }
    }

    AppScreen(
        modifier = modifier,
        actions = onEditModeChange?.let { onChange ->
            { EditModeToggle(editMode = isAdmin, onEditModeChange = onChange) }
        },
        // Change language - goes back to the language selection screen
        trailingActions = { ChangeLanguageLink(onClick = onChangeLanguage) }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // App heading
            Text(
                text = "Learn ${language.displayName}",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = CustomColors.textColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Aprende ${language.displayName}",
                fontSize = 15.sp,
                color = CustomColors.secondaryTextColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Category admin link, only shown in edit mode
            if (isAdmin) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    AdminLink(text = "+ New category", onClick = onCreateCategory)
                }
            }

            // Subject list heading, with the add link alongside it in edit mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your subjects",
                    modifier = Modifier.weight(1f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CustomColors.textColor
                )

                if (isAdmin) {
                    AdminLink(text = "+ Add", onClick = onCreateSubject)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scrollable subject list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(sortedSubjects, key = { it.id }) { sub ->
                    SubjectCard(subject = sub, onClick = { onSubjectClick(sub.id) })
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
                        LoadError(
                            message = errorMessage,
                            onRetry = { loadAttempt++ },
                            modifier = Modifier.padding(vertical = 16.dp)
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
