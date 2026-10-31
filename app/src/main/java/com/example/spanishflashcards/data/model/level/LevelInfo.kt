package com.example.spanishflashcards.data.model.level

// Item from GET /levels.
// code is the CEFR value sent as `level` on subject create/update ("A1" - "C2");
// name is an English label, e.g. "Upper Intermediate".
data class LevelInfo(
    val code: String,
    val name: String
)
