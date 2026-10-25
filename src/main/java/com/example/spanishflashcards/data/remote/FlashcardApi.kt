package com.example.spanishflashcards.data.remote

import com.example.spanishflashcards.data.model.lesson.CreateLesson
import com.example.spanishflashcards.data.model.lesson.Lesson
import com.example.spanishflashcards.data.model.subject.CreateSubject
import com.example.spanishflashcards.data.model.subject.CreateSubjectResponse
import com.example.spanishflashcards.data.model.vocab.CreateVocab
import com.example.spanishflashcards.data.model.vocab.EditVocab
import com.example.spanishflashcards.data.model.subject.Subject
import com.example.spanishflashcards.data.model.subject.SubjectNames
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PUT

interface FlashcardApi {

    // SUBJECT
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

    @PUT("/subject/update/{id}")
    suspend fun updateSubject(
        @Path("id") id: Long,
        @Body subject: Subject?
    ): Subject

    @DELETE("/subject/delete/{id}")
    suspend fun deleteSubject(
        @Path("id") id: Long
    ): Response<Unit>

    // VOCAB
    @GET("/vocab/get/{id}")
    suspend fun getVocabById(
        @Path("id") id: Long
    ): EditVocab

    @POST("/vocab/create")
    suspend fun createVocab(
        @Body vocab: CreateVocab
    ): CreateVocab

    @PUT("/vocab/update/{id}")
    suspend fun editVocab(
        @Path("id") id: Long,
        @Body vocab: EditVocab?
    ): EditVocab

    @DELETE("/vocab/delete/{id}")
    suspend fun deleteVocab(
        @Path("id") id: Long
    ): Response<Unit>

    // LESSON
    @GET("/lesson/get/{id}")
    suspend fun getLesson(
        @Path("id") id: Long
    ): Lesson

    @POST("/lesson/create")
    suspend fun createLesson(
        @Body lesson: CreateLesson
        ): Lesson

    @PUT("/lesson/update/{id}")
    suspend fun updateLesson(
        @Path("id") id: Long,
        @Body lesson: Lesson?
    ): Lesson

}