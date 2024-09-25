package com.example.fetchrewardsce.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDAO {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllItems(items: List<ItemEntity>)

    @Query("SELECT * FROM fetch_item ORDER BY list_id ASC, id ASC")
    fun getAllItems(): Flow<List<ItemEntity>>
}