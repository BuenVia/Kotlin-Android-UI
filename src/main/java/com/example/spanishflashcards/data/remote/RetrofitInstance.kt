package com.example.spanishflashcards.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "http://192.168.1.47:8080"
//    private const val BASE_URL = "https://spring-boot-backend-srdb.onrender.com/"

    val api: FlashcardApi by lazy {
        Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FlashcardApi::class.java)
    }

}