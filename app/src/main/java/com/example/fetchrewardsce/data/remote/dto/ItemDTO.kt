package com.example.fetchrewardsce.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ItemDTO(
    val id: Int,
    val listId: Int,
    val name: String?,
)