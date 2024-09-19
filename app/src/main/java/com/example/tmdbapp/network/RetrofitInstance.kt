package com.example.tmdbapp.network

import com.example.tmdbapp.utils.Constants
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

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

  private val json = Json { ignoreUnknownKeys = true }

  val api: ApiService by lazy {
    Retrofit
      .Builder()
      .baseUrl(Constants.BASE_API_URL)
      .client(client)
      .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
      .build()
      .create(ApiService::class.java)
  }
}
