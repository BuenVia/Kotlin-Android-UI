package com.example.spanishflashcards.data.model.subject

// Request body for subject/create.
// level is required: an uppercase CEFR code from GET /levels ("A1" - "C2").
// categoryId is optional; null is left out of the JSON, creating the subject with no category.
data class CreateSubject (
    val subjectName: String,
    val level: String,
    val categoryId: Long? = null
)
