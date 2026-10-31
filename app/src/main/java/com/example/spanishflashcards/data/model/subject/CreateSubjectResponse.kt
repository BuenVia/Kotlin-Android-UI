package com.example.spanishflashcards.data.model.subject

import com.example.spanishflashcards.data.model.category.CategorySummary

data class CreateSubjectResponse (
    val subjectId: Long,
    val subjectName: String,
    val level: String?,
    val category: CategorySummary?
)
