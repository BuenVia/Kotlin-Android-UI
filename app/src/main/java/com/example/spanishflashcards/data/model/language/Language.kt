package com.example.spanishflashcards.data.model.language

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// Languages supported by the API. The API has no endpoint that lists them, so they live here.
// pathSegment is the first part of every request URL, e.g. /es/subject/getall.
// The enum names match the "language" value the API returns in subject JSON ("ES" / "CA").
enum class Language(val pathSegment: String, val displayName: String) {
    ES("es", "Spanish"),
    CA("ca", "Catalan")
}

// The language the app is currently working in. Every API call uses this to build its URL.
// Held as Compose state so screens can react if the language is changed.
object CurrentLanguage {
    var language by mutableStateOf(Language.ES)

    val pathSegment: String
        get() = language.pathSegment
}
