package com.example.spanishflashcards.data.remote

import com.example.spanishflashcards.data.model.lesson.CreateLesson
import com.example.spanishflashcards.data.model.lesson.EditLesson
import com.example.spanishflashcards.data.model.lesson.Lesson
import com.example.spanishflashcards.data.model.subject.CreateSubject
import com.example.spanishflashcards.data.model.subject.CreateSubjectResponse
import com.example.spanishflashcards.data.model.subject.EditSubject
import com.example.spanishflashcards.data.model.vocab.CreateVocab
import com.example.spanishflashcards.data.model.vocab.EditVocab
import com.example.spanishflashcards.data.model.subject.Subject
import com.example.spanishflashcards.data.model.subject.SubjectNames
import com.example.spanishflashcards.data.model.vocab.Vocab
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PUT

// Every path starts with the language segment (es / ca) - pass CurrentLanguage.pathSegment
interface FlashcardApi {

    // SUBJECT
    @GET("/{language}/subject/getsubjectnames")
    suspend fun getSubjectNames(
        @Path("language") language: String
    ): List<SubjectNames>

    @GET("/{language}/subject/get/{id}")
    suspend fun getSubject(
        @Path("language") language: String,
        @Path("id") id: Long
    ): Subject

    @POST("/{language}/subject/create")
    suspend fun createSubject(
        @Path("language") language: String,
        @Body subject: CreateSubject
    ): CreateSubjectResponse

    @PUT("/{language}/subject/update/{id}")
    suspend fun updateSubject(
        @Path("language") language: String,
        @Path("id") id: Long,
        @Body subject: EditSubject
    ): Subject

    @DELETE("/{language}/subject/delete/{id}")
    suspend fun deleteSubject(
        @Path("language") language: String,
        @Path("id") id: Long
    ): Response<Unit>

    // VOCAB
    @GET("/{language}/vocab/get/{id}")
    suspend fun getVocabById(
        @Path("language") language: String,
        @Path("id") id: Long
    ): EditVocab

    @POST("/{language}/vocab/create")
    suspend fun createVocab(
        @Path("language") language: String,
        @Body vocab: CreateVocab
    ): Vocab

    @PUT("/{language}/vocab/update/{id}")
    suspend fun editVocab(
        @Path("language") language: String,
        @Path("id") id: Long,
        @Body vocab: EditVocab?
    ): Vocab

    @DELETE("/{language}/vocab/delete/{id}")
    suspend fun deleteVocab(
        @Path("language") language: String,
        @Path("id") id: Long
    ): Response<Unit>

    // LESSON
    @GET("/{language}/lesson/get/{id}")
    suspend fun getLesson(
        @Path("language") language: String,
        @Path("id") id: Long
    ): EditLesson

    @POST("/{language}/lesson/create")
    suspend fun createLesson(
        @Path("language") language: String,
        @Body createLesson: CreateLesson
        ): Lesson

    @PUT("/{language}/lesson/update/{id}")
    suspend fun updateLesson(
        @Path("language") language: String,
        @Path("id") id: Long,
        @Body editLesson: EditLesson?
    ): Lesson

}
