package com.example.fetchrewardsce.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fetch_item",
    indices = [
        Index(value = ["list_id"], unique = false),
    ]
)
data class ItemEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "list_id") val listId: Int,
    val name: String,
)