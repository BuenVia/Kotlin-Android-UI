package com.example.spanishflashcards.data.model.EditLesson

data class EditLesson (
    val id: Long,
    val lessonTitle: String,
    val body: String,
    val subjectId: Long
)