
package com.example.spanishflashcards.ui.screens.subject

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.data.model.category.CategorySummary
import com.example.spanishflashcards.data.model.language.CurrentLanguage
import com.example.spanishflashcards.data.model.level.LevelInfo
import com.example.spanishflashcards.data.remote.RetrofitInstance
import com.example.spanishflashcards.data.model.subject.CreateSubject
import kotlinx.coroutines.launch
import com.example.spanishflashcards.ui.utils.AppScreen
import com.example.spanishflashcards.ui.utils.ScreenTitle
import com.example.spanishflashcards.ui.utils.LoadError
import com.example.spanishflashcards.ui.utils.OptionDropdown
import com.example.spanishflashcards.ui.theme.CustomColors
import com.example.spanishflashcards.ui.theme.ButtonShape
import com.example.spanishflashcards.ui.theme.primaryButtonColors
import com.example.spanishflashcards.ui.theme.appTextFieldColors

@Composable
fun CreateFunction(onBack: () -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    // Saveable so typed text and choices survive rotating the screen
    var subjectName by rememberSaveable {
        mutableStateOf("")
    }

    // CEFR code, e.g. "B2". Required by the API, so there is no default - the user must pick one
    var selectedLevel by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    // null = no category
    var selectedCategoryId by rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    // Picker options, loaded from the API
    var levels by remember {
        mutableStateOf<List<LevelInfo>>(emptyList())
    }

    var categories by remember {
        mutableStateOf<List<CategorySummary>>(emptyList())
    }

    var isLoadingOptions by remember {
        mutableStateOf(true)
    }

    var loadError by remember {
        mutableStateOf("")
    }

    // Bumped by the Retry button to run the load again
    var loadAttempt by remember {
        mutableIntStateOf(0)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    LaunchedEffect(loadAttempt) {
        isLoadingOptions = true
        loadError = ""

        try {
            levels = RetrofitInstance.api.getLevels()
            categories = RetrofitInstance.api.getCategoryNames(CurrentLanguage.pathSegment)
                .sortedBy { it.categoryName.orEmpty().lowercase() }
        } catch (e: Exception) {
            e.printStackTrace()
            loadError = "Unable to load levels and categories. Please try again."
        } finally {
            isLoadingOptions = false
        }
    }

    // "No category" first, then the language's categories
    val categoryOptions: List<CategorySummary?> = remember(categories) { listOf(null) + categories }

    fun levelLabel(level: LevelInfo) = "${level.code} - ${level.name}"
    fun categoryLabel(category: CategorySummary?) = category?.categoryName ?: "No category"

    AppScreen(onBack = onBack, backEnabled = !isLoading) {

        if (isLoadingOptions) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = CustomColors.primaryColor
            )
            return@AppScreen
        }

        if (loadError.isNotEmpty()) {
            LoadError(
                message = loadError,
                onRetry = { loadAttempt++ },
                modifier = Modifier.align(Alignment.Center)
            )
            return@AppScreen
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Heading
            ScreenTitle(text = "Create a Subject")

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Give your vocabulary collection a name.",
                fontSize = 14.sp,
                color = CustomColors.secondaryTextColor
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Subject name input
            TextField(
                value = subjectName,
                onValueChange = {
                    subjectName = it
                    errorMessage = ""
                },
                label = {
                    Text("Subject name")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading,
                shape = ButtonShape,
                colors = appTextFieldColors(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Level picker (required)
            OptionDropdown(
                label = "Level",
                selectedLabel = levels.firstOrNull { it.code == selectedLevel }?.let(::levelLabel).orEmpty(),
                options = levels,
                optionLabel = ::levelLabel,
                onSelected = {
                    selectedLevel = it.code
                    errorMessage = ""
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category picker (optional)
            OptionDropdown(
                label = "Category",
                selectedLabel = categoryLabel(categories.firstOrNull { it.id == selectedCategoryId }),
                options = categoryOptions,
                optionLabel = ::categoryLabel,
                onSelected = { selectedCategoryId = it?.id },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Error message
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    fontSize = 14.sp,
                    color = CustomColors.errorColor
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Create button
            Button(
                onClick = {
                    keyboardController?.hide()

                    val name = subjectName.trim()
                    val level = selectedLevel

                    when {
                        name.isEmpty() -> errorMessage = "Please enter a subject name."
                        level == null -> errorMessage = "Please choose a level."
                        else -> coroutineScope.launch {
                            isLoading = true
                            errorMessage = ""

                            val result = handleSubjectSubmit(
                                CreateSubject(
                                    subjectName = name,
                                    level = level,
                                    categoryId = selectedCategoryId
                                )
                            )

                            isLoading = false

                            if (result) {
                                onBack()
                            } else {
                                errorMessage =
                                    "Unable to create the subject. Please try again."
                            }
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = ButtonShape,
                colors = primaryButtonColors()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(22.dp),
                        color = CustomColors.surfaceColor,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Create Subject",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

suspend fun handleSubjectSubmit(
    subjectVal: CreateSubject
): Boolean {

    return try {
        if (subjectVal.subjectName.isBlank()) {
            false
        } else {
            RetrofitInstance.api.createSubject(CurrentLanguage.pathSegment, subjectVal)
            true
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

@Composable
fun editSubject(subjectId: Long, onBack: () -> Unit) {
    // TODO write logic for editing a subject
}

@Composable
fun deleteSubject(subjectId: Long, onBack: () -> Unit) {
    // TODO write logic for deleting a subject
}