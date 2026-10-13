package com.example.spanishflashcards.api

import com.example.spanishflashcards.model.CreateSubject
import com.example.spanishflashcards.model.CreateSubjectResponse
import com.example.spanishflashcards.model.CreateVocab
import com.example.spanishflashcards.model.Subject
import com.example.spanishflashcards.model.SubjectNames
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Body

interface FlashcardApi {

    @GET("/subject/getsubjectnames")
    suspend fun getSubjectNames(): List<SubjectNames>

    @GET("/subject/get/{id}")
    suspend fun getSubject(
        @Path("id") id: Long
    ): Subject

    @POST("/subject/create")
    suspend fun createSubject(
        @Body subject: CreateSubject
    ): CreateSubjectResponse

    @POST("/vocab/create")
    suspend fun createVocab(
        @Body vocab: CreateVocab
    ): CreateVocab

}