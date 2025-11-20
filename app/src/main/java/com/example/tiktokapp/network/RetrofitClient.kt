package com.example.tiktokapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object RetrofitClient {

    private const val BASE_URL = "https://young-wood-7919.bmg-laurent.workers.dev/"


    val api: WebService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WebService::class.java)
}
