package com.example.spanishflashcards.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.spanishflashcards.ui.screens.exam.ExamFunction
import com.example.spanishflashcards.ui.screens.home.HomeScreen
import com.example.spanishflashcards.ui.screens.learn.LearnFunction
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
fun AppNavHost(isAdmin: Boolean, modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    // Only pop once the current screen has settled. While a screen is animating out its
    // back button can still be tapped, and a second pop would remove Home and leave a blank screen.
    val goBack: () -> Unit = {
        if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
            navController.popBackStack()
        }
    }

    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier
    ) {

        composable<HomeRoute> {
            HomeScreen(
                isAdmin = isAdmin,
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
                onBack = goBack,
                onLearn = { navController.navigate(LearnRoute(subjectId)) },
                onExam = { navController.navigate(ExamRoute(subjectId)) },
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

        composable<LearnRoute> { entry ->
            LearnFunction(entry.toRoute<LearnRoute>().subjectId, onBack = goBack)
        }

        composable<ExamRoute> { entry ->
            ExamFunction(entry.toRoute<ExamRoute>().subjectId, onBack = goBack)
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
