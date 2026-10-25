package com.example.spanishflashcards.data.model.subject

import com.example.spanishflashcards.data.model.lesson.Lesson
import com.example.spanishflashcards.data.model.vocab.Vocab

data class Subject (
    val id: Long,
    val subjectName: String,
    val vocabs: List<Vocab>,
    val lesson: Lesson
    )