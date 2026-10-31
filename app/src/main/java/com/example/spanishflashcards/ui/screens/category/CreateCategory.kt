package com.example.spanishflashcards.ui.screens.category

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.data.model.category.CreateCategory
import com.example.spanishflashcards.data.model.language.CurrentLanguage
import com.example.spanishflashcards.data.remote.RetrofitInstance
import com.example.spanishflashcards.ui.theme.ButtonShape
import com.example.spanishflashcards.ui.theme.CustomColors
import com.example.spanishflashcards.ui.theme.appTextFieldColors
import com.example.spanishflashcards.ui.theme.primaryButtonColors
import com.example.spanishflashcards.ui.utils.AppScreen
import com.example.spanishflashcards.ui.utils.ScreenTitle
import kotlinx.coroutines.launch

// Creates a category in the current language. Subjects are added to it separately.
@Composable
fun CreateCategoryView(onBack: () -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    // Saveable so typed text survives rotating the screen
    var categoryName by rememberSaveable {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    fun submit() {
        keyboardController?.hide()

        // The API stores blank names without complaint, so check here
        val name = categoryName.trim()
        if (name.isEmpty()) {
            errorMessage = "Please enter a category name."
            return
        }

        coroutineScope.launch {
            isLoading = true
            errorMessage = ""

            val result = handleCategorySubmit(CreateCategory(categoryName = name))

            isLoading = false

            if (result) {
                onBack()
            } else {
                errorMessage = "Unable to create the category. Please try again."
            }
        }
    }

    AppScreen(onBack = onBack, backEnabled = !isLoading) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Heading
            ScreenTitle(text = "Create a Category")

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Group related subjects under one name.",
                fontSize = 14.sp,
                color = CustomColors.secondaryTextColor
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Category name input
            TextField(
                value = categoryName,
                onValueChange = {
                    categoryName = it
                    errorMessage = ""
                },
                label = {
                    Text("Category name")
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
                onClick = { submit() },
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
                        text = "Create Category",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

suspend fun handleCategorySubmit(category: CreateCategory): Boolean {
    return try {
        RetrofitInstance.api.createCategory(CurrentLanguage.pathSegment, category)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
