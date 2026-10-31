package com.example.spanishflashcards.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.spanishflashcards.ui.screens.practice.PracticeView
import com.example.spanishflashcards.data.model.language.CurrentLanguage
import com.example.spanishflashcards.ui.screens.home.HomeScreen
import com.example.spanishflashcards.ui.screens.language.LanguageSelectScreen
import com.example.spanishflashcards.ui.screens.revise.ReviseView
import com.example.spanishflashcards.ui.screens.lesson.CreateLessonView
import com.example.spanishflashcards.ui.screens.lesson.EditLessonView
import com.example.spanishflashcards.ui.screens.subject.CreateFunction
import com.example.spanishflashcards.ui.screens.subject.SubjectViewFunction
import com.example.spanishflashcards.ui.screens.subject.ViewSubjectEditDelete
import com.example.spanishflashcards.ui.screens.vocab.CreateVocabulary
import com.example.spanishflashcards.ui.screens.vocab.DeleteType
import com.example.spanishflashcards.ui.screens.vocab.ViewVocabDelete
import com.example.spanishflashcards.ui.screens.vocab.ViewVocabFunction

// Single place that decides which screen is shown.
// Screens only report what the user wants to do via callbacks.
// Data is reloaded when returning to a screen, as its LaunchedEffect runs again.
@Composable
fun AppNavHost(adminAvailable: Boolean, modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    // Admin editing controls, switched on and off from the Edit mode switch on Home and Subject.
    // One value for the whole app so the setting carries across screens; saveable so it survives rotation.
    var editMode by rememberSaveable { mutableStateOf(false) }
    val isAdmin = adminAvailable && editMode
    // null hides the switch
    val onEditModeChange: ((Boolean) -> Unit)? = if (adminAvailable) { { editMode = it } } else null

    // Only pop once the current screen has settled. While a screen is animating out its
    // back button can still be tapped, and a second pop would remove Home and leave a blank screen.
    val goBack: () -> Unit = {
        if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
            navController.popBackStack()
        }
    }

    NavHost(
        navController = navController,
        startDestination = LanguageSelectRoute,
        modifier = modifier
    ) {

        composable<LanguageSelectRoute> {
            // Only offer Back when opened from Home - at startup there is nothing to go back to
            val canGoBack = navController.previousBackStackEntry != null
            LanguageSelectScreen(
                onBack = if (canGoBack) goBack else null,
                onLanguageSelected = { language ->
                    if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        CurrentLanguage.language = language
                        // Clear the whole back stack so Home starts fresh with the new language's subjects
                        navController.navigate(HomeRoute) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable<HomeRoute> {
            HomeScreen(
                isAdmin = isAdmin,
                onEditModeChange = onEditModeChange,
                onChangeLanguage = { navController.navigate(LanguageSelectRoute) },
                onSubjectClick = { subjectId -> navController.navigate(SubjectDetailRoute(subjectId)) },
                onCreateSubject = { navController.navigate(CreateSubjectRoute) }
            )
        }

        composable<CreateSubjectRoute> {
            CreateFunction(onBack = goBack)
        }

        composable<SubjectDetailRoute> { entry ->
            val subjectId = entry.toRoute<SubjectDetailRoute>().subjectId
            SubjectViewFunction(
                subjectId = subjectId,
                isAdmin = isAdmin,
                onEditModeChange = onEditModeChange,
                onBack = goBack,
                onRevise = { navController.navigate(ReviseRoute(subjectId)) },
                onPractice = { navController.navigate(PracticeRoute(subjectId)) },
                onCreateVocab = { navController.navigate(CreateVocabRoute(subjectId)) },
                onEditVocab = { vocabId -> navController.navigate(EditVocabRoute(vocabId)) },
                onCreateLesson = { navController.navigate(CreateLessonRoute(subjectId)) },
                onEditLesson = { lessonId -> navController.navigate(EditLessonRoute(lessonId, subjectId)) },
                onEditSubject = { navController.navigate(EditSubjectRoute(subjectId)) }
            )
        }

        composable<EditSubjectRoute> { entry ->
            val subjectId = entry.toRoute<EditSubjectRoute>().subjectId
            ViewSubjectEditDelete(
                subjectId = subjectId,
                onBack = goBack,
                onDelete = { navController.navigate(ConfirmDeleteRoute(DeleteType.SUBJECT, subjectId)) }
            )
        }

        composable<ReviseRoute> { entry ->
            ReviseView(entry.toRoute<ReviseRoute>().subjectId, onBack = goBack)
        }

        composable<PracticeRoute> { entry ->
            PracticeView(entry.toRoute<PracticeRoute>().subjectId, onBack = goBack)
        }

        composable<CreateVocabRoute> { entry ->
            CreateVocabulary(entry.toRoute<CreateVocabRoute>().subjectId, onBack = goBack)
        }

        composable<EditVocabRoute> { entry ->
            val vocabId = entry.toRoute<EditVocabRoute>().vocabId
            ViewVocabFunction(
                vocabId = vocabId,
                onBack = goBack,
                onDelete = { navController.navigate(ConfirmDeleteRoute(DeleteType.VOCAB, vocabId)) }
            )
        }

        composable<CreateLessonRoute> { entry ->
            CreateLessonView(entry.toRoute<CreateLessonRoute>().subjectId, onBack = goBack)
        }

        composable<EditLessonRoute> { entry ->
            val route = entry.toRoute<EditLessonRoute>()
            EditLessonView(route.lessonId, route.subjectId, onBack = goBack)
        }

        composable<ConfirmDeleteRoute> { entry ->
            val route = entry.toRoute<ConfirmDeleteRoute>()
            ViewVocabDelete(
                delType = route.type,
                itemId = route.itemId,
                onCancel = goBack,
                onDeleted = {
                    // The deleted item's screens no longer make sense, so skip past them
                    when (route.type) {
                        DeleteType.VOCAB -> navController.popBackStack<SubjectDetailRoute>(inclusive = false)
                        DeleteType.SUBJECT -> navController.popBackStack<HomeRoute>(inclusive = false)
                    }
                }
            )
        }
    }
}
