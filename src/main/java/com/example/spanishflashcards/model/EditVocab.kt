package com.example.spanishflashcards.model

data class EditVocab (
    val id: Long,
    val esp: String,
    val eng: String,
    val subjectId: Long
)