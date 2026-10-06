package com.example.spanishflashcards.api

import com.example.spanishflashcards.model.Subject
import com.example.spanishflashcards.model.SubjectNames
import retrofit2.http.GET
import retrofit2.http.Path

interface FlashcardApi {

    @GET("/subject/getsubjectnames")
    suspend fun getSubjectNames(): List<SubjectNames>

    @GET("/subject/get/{id}")
    suspend fun getSubject(
        @Path("id") id: Long
    ): Subject

}