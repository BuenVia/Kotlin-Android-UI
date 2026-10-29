package com.example.spanishflashcards.data.model.vocab

data class EditVocab (
    val id: Long,
    val term: String,
    val translation: String,
    val subjectId: Long
)