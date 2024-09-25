package com.example.fetchrewardsce.data.remote

import com.example.fetchrewardsce.data.remote.dto.FetchResponse
import com.example.fetchrewardsce.data.remote.dto.ItemDTO
import retrofit2.Response
import retrofit2.http.GET


interface FetchAPI {

    @GET("/hiring.json")
    suspend fun getData(): Response<List<ItemDTO>>

}