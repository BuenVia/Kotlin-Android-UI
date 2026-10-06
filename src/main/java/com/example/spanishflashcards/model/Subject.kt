package com.example.spanishflashcards.model

data class Subject (
    val id: Long,
    val subjectName: String,
    val vocabs: List<Vocab>
    )