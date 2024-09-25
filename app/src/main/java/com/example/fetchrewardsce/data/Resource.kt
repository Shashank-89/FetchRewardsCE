package com.example.fetchrewardsce.data

sealed class Resource<T>(
    val data: T? = null,
    val message: String = "",
) {
    class Success<T>(data: T): Resource<T>(data = data)
    class Error<T>(message: String, data: T? = null): Resource<T>(data, message)
    class Loading<T>: Resource<T>()
}