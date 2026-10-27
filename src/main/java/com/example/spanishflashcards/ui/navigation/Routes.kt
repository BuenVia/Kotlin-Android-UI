package com.example.spanishflashcards.ui.navigation

import com.example.spanishflashcards.ui.screens.vocab.DeleteType
import kotlinx.serialization.Serializable

// Every screen in the app. Arguments a screen needs are passed as fields.

@Serializable
object HomeRoute

@Serializable
object CreateSubjectRoute

@Serializable
data class SubjectDetailRoute(val subjectId: Long)

@Serializable
data class EditSubjectRoute(val subjectId: Long)

@Serializable
data class LearnRoute(val subjectId: Long)

@Serializable
data class ExamRoute(val subjectId: Long)

@Serializable
data class CreateVocabRoute(val subjectId: Long)

@Serializable
data class EditVocabRoute(val vocabId: Long)

@Serializable
data class CreateLessonRoute(val subjectId: Long)

@Serializable
data class EditLessonRoute(val lessonId: Long, val subjectId: Long)

@Serializable
data class ConfirmDeleteRoute(val type: DeleteType, val itemId: Long)
