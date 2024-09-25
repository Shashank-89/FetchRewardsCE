package com.example.fetchrewardsce.data

import com.example.fetchrewardsce.data.local.ItemEntity
import com.example.fetchrewardsce.data.remote.dto.ItemDTO

fun ItemDTO.toItemEntity() = ItemEntity(
    id,
    listId,
    name ?: "",
)