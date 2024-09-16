package com.example.tmdbapp.network

import com.example.tmdbapp.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val client by lazy {
        val logging =
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        OkHttpClient
            .Builder()
            .addInterceptor(logging)
            .build()
    }

    val api: ApiService by lazy {
        Retrofit
            .Builder()
            .baseUrl(Constants.BASE_API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
