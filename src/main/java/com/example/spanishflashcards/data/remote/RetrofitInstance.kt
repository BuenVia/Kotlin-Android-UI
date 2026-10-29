package com.example.spanishflashcards.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

//    private const val BASE_URL = "http://192.168.1.47:8080"
    private const val BASE_URL = "https://spring-boot-backend-srdb.onrender.com/"

    // The Render server sleeps when idle and can take up to a minute to wake,
    // so the 10 second OkHttp defaults would fail the first request after a quiet period
    private const val TIMEOUT_SECONDS = 60L

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    val api: FlashcardApi by lazy {
        Retrofit.Builder().baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FlashcardApi::class.java)
    }

}
