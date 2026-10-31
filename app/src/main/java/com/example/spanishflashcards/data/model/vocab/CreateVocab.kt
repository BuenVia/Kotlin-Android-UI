package com.example.spanishflashcards.data.model.vocab

data class CreateVocab (
    val term: String,
    val translation: String,
    val subjectId: Long
)