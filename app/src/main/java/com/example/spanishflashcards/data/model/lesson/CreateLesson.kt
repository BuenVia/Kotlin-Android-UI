package com.example.spanishflashcards.data.model.lesson

data class CreateLesson (
    val lessonTitle: String,
    val body: String,
    val subjectId: Long
)