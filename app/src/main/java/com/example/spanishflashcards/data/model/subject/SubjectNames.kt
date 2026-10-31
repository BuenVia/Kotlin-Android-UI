package com.example.spanishflashcards.data.model.subject

import com.example.spanishflashcards.data.model.category.CategorySummary

// Item from subject/getsubjectnames.
// level is a CEFR code ("A1" - "C2"); null for older subjects never given one.
// category is null for subjects not in a category.
data class SubjectNames (
    val id: Long,
    val subjectName: String?,
    val level: String?,
    val category: CategorySummary?
)
