package com.example.fetchrewardsce.data.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class RemoteAPIs {

    companion object{
        private val retrofit by lazy {
            val httpClient = OkHttpClient.Builder().build()

            Retrofit.Builder()
                .baseUrl("https://fetch-hiring.s3.amazonaws.com")
                .client(httpClient)
                .addConverterFactory(
                    Json.asConverterFactory("application/json".toMediaType())
                ).build()
        }

        val fetchAPI by lazy {
            retrofit.create(FetchAPI::class.java)
        }
    }

}